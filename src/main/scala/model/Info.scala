package org.kainovk
package model

case class Info(secid: String,
                regnumber: String,
                name: Option[String],
                emitentTitle: String,
                tradedate: String,
                numtrades: Int,
                open: Option[Double],
                close: Option[Double])