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
package values


/** StrictValues is a library of implicit ValueReaders and ValueWriters
  * for common Java / Scala types.
  *
  * Usually you don't need to work with ValueReader / ValueWriter from
  * your code, they get called under the hood.
  */
object StrictValues extends BaseSerialization with ScalaTypesSerialization