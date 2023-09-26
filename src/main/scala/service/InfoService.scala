package org.kainovk
package service

import model.Info

import cats.effect.IO

trait InfoService {

  def getCombinedInfo(secid: String): IO[List[Info]]

  def getInfo: IO[List[Info]]
}
