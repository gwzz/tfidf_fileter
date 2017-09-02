import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import collection.mutable.HashMap
import org.apache.spark.mllib.linalg.{ SparseVector => SV }
import java.io._
import scala.collection.mutable.StringBuilder
import scala.collection.mutable.ListBuffer

object TFIDFExample {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("TFIDFExample").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val documents: RDD[Seq[String]] = sc.textFile("/Users/zhuwei/studying/scala/tfidf/data/medical_noids.txt")
      .map(_.split(";").toSeq)

    val hashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)
    tf.cache()
    val idf = new IDF().fit(tf)
    val tfidf: RDD[Vector] = idf.transform(tf)
    var outNoValue = new ListBuffer[String]()
    var outWithValue = new ListBuffer[String]()

    val dict: HashMap[Long,String] = new HashMap() 
    val words = documents.collect.flatten.distinct
    words.foreach{
        case word => 
            dict.put(hashingTF.indexOf(word),word)
    }
    var i: Int = 0
    val out = new BufferedWriter(new FileWriter("tfidf"))
    tfidf.foreach{
        case document =>
            val str = StringBuilder.newBuilder
            val d = document.asInstanceOf[SV]
            val l = d.indices.zip(d.values).filter(_._2 > 3)
            l.foreach{ case (hashcode,value) =>
                try{
                    // println(dict(hashcode)+": "+value)      
                    str.append(";" + dict(hashcode))
                }       
                catch {
                  case e:Exception => println(e)
                }
            }
            println(str.toString)
            outNoValue += str.toString

        }
    // val outNoValueList = outNoValue.toList
    // println(outNoValueList.size)
    // val outWithValueList = outWithValue.toList

    outNoValue.foreach(println(_))
    out.close
    // spark.mllib IDF implementation provides an option for ignoring terms which occur in less than
    // a minimum number of documents. In such cases, the IDF for these terms is set to 0.
    // This feature can be used by passing the minDocFreq value to the IDF constructor.
    val idfIgnore = new IDF(minDocFreq = 2).fit(tf)
    val tfidfIgnore: RDD[Vector] = idfIgnore.transform(tf)
    

    // println("tfidfIgnore: ")
    // tfidfIgnore.foreach(x => println(x))
    println("size:" + tfidf.count)
    sc.stop()
  }
}
