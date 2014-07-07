package app

import play.modules.gresource._

object apiRoutes extends BaseApiRoutes {

  override val subRoutes = List(
    "/todos" -> Todos.httpApi
  )
}
