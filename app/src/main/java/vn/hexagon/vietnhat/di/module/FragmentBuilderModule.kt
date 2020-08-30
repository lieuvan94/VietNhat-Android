package vn.hexagon.vietnhat.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.hexagon.vietnhat.ui.auth.LoginFragment
import vn.hexagon.vietnhat.ui.auth.RegisterFragment
import vn.hexagon.vietnhat.ui.auth.VerifyFragment
import vn.hexagon.vietnhat.ui.chat.ChatDetailFragment
import vn.hexagon.vietnhat.ui.chat.ChatListFragment
import vn.hexagon.vietnhat.ui.comment.CommentFragment
import vn.hexagon.vietnhat.ui.detail.PostDetailFragment
import vn.hexagon.vietnhat.ui.favourite.FavouriteFragment
import vn.hexagon.vietnhat.ui.favourite.LikedListFragment
import vn.hexagon.vietnhat.ui.home.HomeFragment
import vn.hexagon.vietnhat.ui.list.ads.AdsListFragment
import vn.hexagon.vietnhat.ui.list.car.CarListFragment
import vn.hexagon.vietnhat.ui.list.deliver.DeliverDetailFragment
import vn.hexagon.vietnhat.ui.list.deliver.DeliverListFragment
import vn.hexagon.vietnhat.ui.list.job.JobListFragment
import vn.hexagon.vietnhat.ui.list.mart.MartDetailFragment
import vn.hexagon.vietnhat.ui.list.mart.MartListFragment
import vn.hexagon.vietnhat.ui.list.mypost.ManagePostFragment
import vn.hexagon.vietnhat.ui.list.news.NewsListFragment
import vn.hexagon.vietnhat.ui.list.news.WebDetailFragment
import vn.hexagon.vietnhat.ui.list.phone.PhoneListFragment
import vn.hexagon.vietnhat.ui.list.restaurant.RestaurantListFragment
import vn.hexagon.vietnhat.ui.list.spa.SpaListFragment
import vn.hexagon.vietnhat.ui.list.support.SupportListFragment
import vn.hexagon.vietnhat.ui.list.translator.TranslatorListFragment
import vn.hexagon.vietnhat.ui.list.travel.TravelListFragment
import vn.hexagon.vietnhat.ui.list.visa.VisaListFragment
import vn.hexagon.vietnhat.ui.post.ads.AdsPostFragment
import vn.hexagon.vietnhat.ui.post.car.CarPostFragment
import vn.hexagon.vietnhat.ui.post.deliver.DeliverPostFragment
import vn.hexagon.vietnhat.ui.post.job.JobPostFragment
import vn.hexagon.vietnhat.ui.post.mart.MartPostFragment
import vn.hexagon.vietnhat.ui.post.phone.PhonePostFragment
import vn.hexagon.vietnhat.ui.post.restaurant.RestaurantPostFragment
import vn.hexagon.vietnhat.ui.post.spa.SpaPostFragment
import vn.hexagon.vietnhat.ui.post.trans.TransPostFragment
import vn.hexagon.vietnhat.ui.post.travel.TravelPostFragment
import vn.hexagon.vietnhat.ui.post.visa.VisaPostFragment
import vn.hexagon.vietnhat.ui.setting.SettingFragment
import vn.hexagon.vietnhat.ui.setting.account.AccountInfoFragment
import vn.hexagon.vietnhat.ui.setting.account.password.RequestSmsCodeFragment
import vn.hexagon.vietnhat.ui.setting.guide.GuideDetailFragment
import vn.hexagon.vietnhat.ui.setting.guide.GuideListFragment
import vn.hexagon.vietnhat.ui.setting.notification.NotifyDetailFragment
import vn.hexagon.vietnhat.ui.setting.notification.NotifyFragment
import vn.hexagon.vietnhat.ui.setting.rate.RateFragment
import vn.hexagon.vietnhat.ui.splash.SplashFragment
import vn.hexagon.vietnhat.ui.zoom.ZoomFragment

/**
 * Created by NhamVD on 2019-08-17.
 */
@Suppress("unused")
@Module
abstract class FragmentBuilderModule {

  @ContributesAndroidInjector
  abstract fun contributeHomeFragment(): HomeFragment

  @ContributesAndroidInjector
  abstract fun contributeMartFragment(): MartListFragment

  @ContributesAndroidInjector
  abstract fun contributeMartDetailFragment(): MartDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeSettingFragment(): SettingFragment

  @ContributesAndroidInjector
  abstract fun contributeAccountInfoFragment(): AccountInfoFragment

  @ContributesAndroidInjector
  abstract fun contributeLoginFragment(): LoginFragment

  @ContributesAndroidInjector
  abstract fun contributeRegisterFragment(): RegisterFragment

  @ContributesAndroidInjector
  abstract fun contributeTransPostFragment(): TransPostFragment

  @ContributesAndroidInjector
  abstract fun contributeJobPostFragment(): JobPostFragment

  @ContributesAndroidInjector
  abstract fun contributeTravelPostFragment(): TravelPostFragment

  @ContributesAndroidInjector
  abstract fun contributeVisaPostFragment(): VisaPostFragment

  @ContributesAndroidInjector
  abstract fun contributeAdsPostFragment(): AdsPostFragment

  @ContributesAndroidInjector
  abstract fun contributeDeliverPostFragment(): DeliverPostFragment

  @ContributesAndroidInjector
  abstract fun contributeSpaPostFragment(): SpaPostFragment

  @ContributesAndroidInjector
  abstract fun contributeJobListFragment(): JobListFragment

  @ContributesAndroidInjector
  abstract fun contributeTranslatorListFragment(): TranslatorListFragment

  @ContributesAndroidInjector
  abstract fun contributeNewListFragment(): NewsListFragment

  @ContributesAndroidInjector
  abstract fun contributeTravelListFragment(): TravelListFragment

  @ContributesAndroidInjector
  abstract fun contributeCarListFragment(): CarListFragment

  @ContributesAndroidInjector
  abstract fun contributeRestaurantListFragment(): RestaurantListFragment

  @ContributesAndroidInjector
  abstract fun contributeSpaListFragment(): SpaListFragment

  @ContributesAndroidInjector
  abstract fun contributePhoneListFragment(): PhoneListFragment

  @ContributesAndroidInjector
  abstract fun contributeVisaListFragment(): VisaListFragment

  @ContributesAndroidInjector
  abstract fun contributeDeliverListFragment(): DeliverListFragment

  @ContributesAndroidInjector
  abstract fun contributeSupportListFragment(): SupportListFragment

  @ContributesAndroidInjector
  abstract fun contributeAdsListFragment(): AdsListFragment

  @ContributesAndroidInjector
  abstract fun contributeFavListFragment(): FavouriteFragment

  @ContributesAndroidInjector
  abstract fun contributePostDetailFragment(): PostDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeGuideListFragment(): GuideListFragment

  @ContributesAndroidInjector
  abstract fun contributeGuideDetailFragment(): GuideDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeRateFragment(): RateFragment

  @ContributesAndroidInjector
  abstract fun contributeNotifyFragment(): NotifyFragment

  @ContributesAndroidInjector
  abstract fun contributeRestaurantPostFragment(): RestaurantPostFragment

  @ContributesAndroidInjector
  abstract fun contributeCarPostFragment(): CarPostFragment

  @ContributesAndroidInjector
  abstract fun contributeMartPostFragment(): MartPostFragment

  @ContributesAndroidInjector
  abstract fun contributePhonePostFragment(): PhonePostFragment

  @ContributesAndroidInjector
  abstract fun contributeManagePostFragment(): ManagePostFragment

  @ContributesAndroidInjector
  abstract fun contributeChatListFragment(): ChatListFragment

  @ContributesAndroidInjector
  abstract fun contributeChatDetailFragment(): ChatDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeDeliverDetailFragment(): DeliverDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeRequestSmsFragment(): RequestSmsCodeFragment

  @ContributesAndroidInjector
  abstract fun contributeNotifyDetailFragment(): NotifyDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeVerifyFragment(): VerifyFragment

  @ContributesAndroidInjector
  abstract fun contributeSplashFragment(): SplashFragment

  @ContributesAndroidInjector
  abstract fun contributeNewsDetailFragment(): WebDetailFragment

  @ContributesAndroidInjector
  abstract fun contributeCommentFragment(): CommentFragment

  @ContributesAndroidInjector
  abstract fun contributeZoomFragment(): ZoomFragment

  @ContributesAndroidInjector
  abstract fun contributeLikedFragment(): LikedListFragment
}