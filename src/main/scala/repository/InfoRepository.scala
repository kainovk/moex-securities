package org.kainovk
package repository

import model.Info

import cats.effect.IO

trait InfoRepository {

  def getInfo: IO[List[Info]]
}
