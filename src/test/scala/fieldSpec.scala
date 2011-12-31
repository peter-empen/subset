/**
 * Copyright (C) 2011 Alexander Azarov <azarov@osinka.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osinka.subset

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import com.mongodb.{DBObject,BasicDBObjectBuilder}
import BasicDBObjectBuilder.start

@RunWith(classOf[JUnitRunner])
class fieldSpec extends Spec with MustMatchers with MongoMatchers with Routines {
  import SmartValues._

  describe("Field") {
    it("has proper basic Java methods") {
      Field[Int]("i") must equal(Field[String]("i"))
      Field[Int]("i").hashCode must equal(Field[String]("i").hashCode)
      Field[Int]("i").toString must startWith("Field")
    }
    it("serializes explicitly") {
      val f = Field[Int]("i")
      (f(10) : DBObject) must containKeyValue("i" -> 10)
    }
    it("serializes implicitly") {
      val f = Field[Int]("i")
      val dbo: DBObject = f -> 10
      dbo must containKeyValue("i" -> 10)
    }
    it("chains serializers 1") {
      val f1 = "i".fieldOf[Int]
      val f2 = "s".fieldOf[String]
      ( (f1 -> 12) ~ (f2 -> "string") : DBObject) must (containKeyValue("i" -> 12) and containKeyValue("s" -> "string"))
    }
    it("chains serializers 2") {
      val obj = ("i".fieldOf[Int] -> 12) ~ ("s".fieldOf[String] -> "string")
      (obj : DBObject) must (containKeyValue("i" -> 12) and containKeyValue("s" -> "string"))
    }
    it("has extractor") {
      val F1 = "i".fieldOf[Int]
      start("i", 10).get match {
        case F1(i) => i must equal(10)
        case _ => fail("must extract field value")
      }
    }
    it("has conjunction extractor") {
      val I = "i".fieldOf[Int]
      val S = "s".fieldOf[String]
      start("i", 10).append("s", "string").get match {
        case I(i) ~ S(s) =>
          i must equal(10)
          s must equal("string")
        case _ =>
          fail("must extract field value")
      }
    }
    it("has tuple extractor") {
      val O = "i".fieldOf[Int] ~ "s".fieldOf[String]
      start("i", 10).append("s", "string").get match {
        case O(i, s) =>
          i must equal(10)
          s must equal("string")
        case _ =>
          fail("must extract field value")
      }
    }
    it("may be attached to Subset") {
      val f_ext = "f".fieldOf[Int]
      object Doc extends Subset[DBObject]("doc") {
        val f = "f".fieldOf[Int]
        val f1 = f_ext.attach
      }
      "f".fieldOf[Int].attach(Doc) must equal(Doc.f)
      Doc.f1 must equal(Doc.f)
    }
    it("may be detached from a Subset") {
      object Doc extends Subset[DBObject]("doc") {
        val f = "f".fieldOf[Int]
      }
      Doc.f.detach must equal("f".fieldOf[Int])
    }
    it("provides a positional operator") {
      object Doc extends Subset[DBObject]("doc") {
        val f = "f".fieldOf[Int]
      }
      Doc.f.first.longName must equal("doc.f.$")
      Doc.f.first.in(Doc).longName must equal("doc.$.f")

      Doc.f.at(1) must equal( Field[Int]("1")(Path("doc" :: "f" :: Nil)) )
      Doc.f.at(1).in(Doc).longName must equal("doc.1.f")
    }
  }
}
