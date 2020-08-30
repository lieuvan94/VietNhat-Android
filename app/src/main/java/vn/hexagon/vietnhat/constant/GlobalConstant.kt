package vn.hexagon.vietnhat.constant


class GlobalConstant : IConstant {
  override fun getProductionURL(): String = ""

  override fun getDevelopmentURL(): String = APIConstant.BASE_SERVER_URL

}