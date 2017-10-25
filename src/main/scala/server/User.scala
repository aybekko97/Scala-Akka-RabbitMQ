package server

import com.aerospike.client.policy.WritePolicy
import com.aerospike.client.{AerospikeClient, Bin, Key}
import spray.json.DefaultJsonProtocol

case class User(name: String, surname: String) {
  def save(): Unit = {
    User.newId += 1
    val client = new AerospikeClient("localhost", 3000)

    val key = new Key("aibek", "notebook_set", User.newId)

    val bin1 = new Bin("name", this.name)
    val bin2 = new Bin("surname", this.surname)

    client.put(new WritePolicy, key, bin1, bin2)
    client.close()
  }
}

object User extends DefaultJsonProtocol{
  var newId: Int = 1
  implicit val userFormat = jsonFormat2(User.apply)

  def get(id: Int): Option[User] = {
    val client = new AerospikeClient("localhost", 3000)

    val key = new Key("aibek", "notebook_set", id)

    val record = client.get(null, key)
    client.close()

    if (record != null) {
      val itr = record.bins.entrySet().iterator()

      var name: Option[String] = None
      var surname: Option[String] = None

      while (itr.hasNext) {
        val entry = itr.next()
        if (entry.getKey.equals("surname")) surname = Some(entry.getValue.toString)
        if (entry.getKey.equals("name")) name = Some(entry.getValue.toString)
      }
      User(name.get, surname.get)
    }
    None
  }
}