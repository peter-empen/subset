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

import com.mongodb.{DBObject,BasicDBObjectBuilder}

/** The low level mechanism for modifying DBObjects.
  * 
  * === Using DBObjectLens in subset ===
  * Most ''subset'' classes are subtypes of `DBObjectLens`, so that it becomes possible to
  * compose them and apply to already existing `DBObject` values.
  * 
  * === Example ===
  * Basically the lens is just a function taking one `DBObject` and returning another.
  * We can compose them into a single DBObjectLens. A DBObjectLens is suitable to modify existing
  * `DBObject`, thus providing interobility with existing code (e.g. Java).
  * 
  * {{{
  * val dbo: DBObject =
  * val lens1 = DBObjectLens.writer[Int]("x", 10)
  * val lens2 = DBObjectLens.writer[String]("s", "str")
  * 
  * val lens = lens1 ~ lens2
  * lens(dbo) must (containKeyValue("x" -> 10) and containKeyValue("s" -> "str"))
  * }}}
  */
trait DBObjectLens extends (DBObject => DBObject) {
  /** Applies this lens to an empty `DBObject`
    * @return `DBObject`
    */
  def get: DBObject = apply(BasicDBObjectBuilder.start.get)

  /** Compose two lenses.
    * @return a composition of lenses
    */
  def ~ (other: DBObjectLens): DBObjectLens = DBObjectLens(this andThen other)

  def prefixString = "DBObjectLens"

  override def toString: String = prefixString+get

  override def equals(o: Any): Boolean =
    PartialFunction.cond(o) { case other: DBObjectLens if prefixString == other.prefixString => get == other.get }

  override def hashCode: Int = get.hashCode
}

/** Provides a factory method to create lenses from `DBObject => DBObject` functions and a couple
  * convenience methods.
  */
object DBObjectLens {
  // Factory object
  def apply(f: DBObject => DBObject): DBObjectLens =
    new DBObjectLens {
      def apply(dbo: DBObject): DBObject = f(dbo)
    }

  implicit def fToDBObjectLens(f: DBObject => DBObject): DBObjectLens = apply(f)
  implicit def lensWriter: ValueWriter[DBObjectLens] = ValueWriter[DBObjectLens](_.get)

  /** Reads a value from `DBObject` by key.
    *
    * This method makes use of [[com.osinka.subset.ValueReader]] implicit to unpack the object correctly.
    * 
    * @see [[com.osinka.subset.ValueReader]]
    */
  def read[T](key: String, dbo: DBObject)(implicit reader: ValueReader[T]): Option[T] =
    Option(dbo.get(key)) flatMap {reader.unpack(_)}

  /** Creates a lens that writes a typed key-value.
    * 
    * Makes use of [[com.osinka.subset.ValueWriter]] implicit.
    * 
    * @see [[com.osinka.subset.ValueWriter]]
    */
  def writer[T](key: String, x: T)(implicit w: ValueWriter[T]): DBObjectLens =
    (dbo: DBObject) => {
        w.pack(x) foreach {dbo.put(key, _)}
        dbo
      }
}

