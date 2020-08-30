package vn.hexagon.vietnhat.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import vn.hexagon.vietnhat.base.di.BaseViewModelFactory
import vn.hexagon.vietnhat.base.di.ViewModelKey
import vn.hexagon.vietnhat.repository.mart.MartDetailViewModel
import vn.hexagon.vietnhat.ui.MainViewModel
import vn.hexagon.vietnhat.ui.auth.LoginFragmentViewModel
import vn.hexagon.vietnhat.ui.auth.RegisterViewModel
import vn.hexagon.vietnhat.ui.chat.ChatDetailViewModel
import vn.hexagon.vietnhat.ui.chat.ChatListViewModel
import vn.hexagon.vietnhat.ui.detail.PostDetailViewModel
import vn.hexagon.vietnhat.ui.example.ExampleActivityViewModel
import vn.hexagon.vietnhat.ui.example.ExampleFragmentViewModel
import vn.hexagon.vietnhat.ui.favourite.FavoriteViewModel
import vn.hexagon.vietnhat.ui.home.HomeViewModel
import vn.hexagon.vietnhat.ui.list.PostListViewModel
import vn.hexagon.vietnhat.ui.list.mart.MartPagedListViewModel
import vn.hexagon.vietnhat.ui.list.mypost.ManagePostViewModel
import vn.hexagon.vietnhat.ui.post.PostViewModel
import vn.hexagon.vietnhat.ui.post.ads.AdsPostViewModel
import vn.hexagon.vietnhat.ui.post.deliver.DeliverPostViewModel
import vn.hexagon.vietnhat.ui.post.job.JobPostViewModel
import vn.hexagon.vietnhat.ui.post.restaurant.ProductPostViewModel
import vn.hexagon.vietnhat.ui.post.trans.TransPostViewModel
import vn.hexagon.vietnhat.ui.post.travel.TravelPostViewModel
import vn.hexagon.vietnhat.ui.post.visa.VisaPostViewModel
import vn.hexagon.vietnhat.ui.setting.account.AccountInfoViewModel
import vn.hexagon.vietnhat.ui.setting.guide.GuideViewModel
import vn.hexagon.vietnhat.ui.setting.notification.NotifyViewModel
import vn.hexagon.vietnhat.ui.setting.rate.RateViewModel

/**
 * Created by NhamVD on 2019-07-28.
 */
@Suppress("unused")
@Module
abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(ExampleActivityViewModel::class)
  abstract fun bindExampleActivityViewModel(userViewModel: ExampleActivityViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(ExampleFragmentViewModel::class)
  abstract fun bindExampleFragmentViewModel(userViewModel: ExampleFragmentViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(HomeViewModel::class)
  abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(MartPagedListViewModel::class)
  abstract fun bindMartViewModel(martViewModel: MartPagedListViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(MartDetailViewModel::class)
  abstract fun bindMartDetailViewModel(martDetailViewModel: MartDetailViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(LoginFragmentViewModel::class)
  abstract fun bindLoginViewModel(loginViewModel: LoginFragmentViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(RegisterViewModel::class)
  abstract fun bindRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(PostViewModel::class)
  abstract fun bindPostViewModel(postViewModel: PostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(TransPostViewModel::class)
  abstract fun bindTransPostViewModel(transPostViewModel: TransPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(JobPostViewModel::class)
  abstract fun bindJobPostViewModel(jobPostViewModel: JobPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(TravelPostViewModel::class)
  abstract fun bindTravelPostViewModel(jobPostViewModel: TravelPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(VisaPostViewModel::class)
  abstract fun bindVisaPostViewModel(jobPostViewModel: VisaPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(AdsPostViewModel::class)
  abstract fun bindAdsPostViewModel(jobPostViewModel: AdsPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(DeliverPostViewModel::class)
  abstract fun bindDeliverPostViewModel(jobPostViewModel: DeliverPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(PostListViewModel::class)
  abstract fun bindJobListViewModel(postListViewModel: PostListViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(FavoriteViewModel::class)
  abstract fun bindFavListViewModel(favListViewModel: FavoriteViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(AccountInfoViewModel::class)
  abstract fun bindAccountInfoViewModel(accountInfoViewModel: AccountInfoViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(GuideViewModel::class)
  abstract fun bindGuideViewModel(guideViewModel: GuideViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(PostDetailViewModel::class)
  abstract fun bindPostDetailViewModel(postDetailViewModel: PostDetailViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(RateViewModel::class)
  abstract fun bindRateViewModel(rateViewModel: RateViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(NotifyViewModel::class)
  abstract fun bindNotifyViewModel(notifyViewModel: NotifyViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(ProductPostViewModel::class)
  abstract fun bindProductPostViewModel(productPostViewModel: ProductPostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(ManagePostViewModel::class)
  abstract fun bindManagePostViewModel(managePostViewModel: ManagePostViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(ChatListViewModel::class)
  abstract fun bindChatListViewModel(chatListViewModel: ChatListViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(ChatDetailViewModel::class)
  abstract fun bindChatDetailViewModel(chatListViewModel: ChatDetailViewModel): ViewModel

  @Binds
  abstract fun bindViewModelFactory(factory: BaseViewModelFactory): ViewModelProvider.Factory
}