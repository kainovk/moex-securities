package org.kainovk
package model

case class SecurityWithId(id: Option[Int],
                          secid: String,
                          regnumber: String,
                          name: String,
                          emitentTitle: String)

case class Security(secid: String,
                    regnumber: String,
                    name: String,
                    emitentTitle: String)