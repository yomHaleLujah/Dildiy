package com.yome.dildiy.di

import SoldRepository
import com.yome.dildiy.networking.DeliveryRepository
import com.yome.dildiy.networking.GetCartRepository
import com.yome.dildiy.networking.LoginRepository
import com.yome.dildiy.networking.OrderRepository
import com.yome.dildiy.networking.PaymentRepository
import com.yome.dildiy.networking.ProductDetailRepository
import com.yome.dildiy.networking.ProductRepository
import com.yome.dildiy.networking.ProfileRepository
import com.yome.dildiy.networking.RegisterRepository
import com.yome.dildiy.networking.SearchRepository
import com.yome.dildiy.networking.ShoppingCartRepository
import com.yome.dildiy.networking.UploadProductRepository
import com.yome.dildiy.ui.ecommerce.shoppingCart.ShoppingCartViewModel
import com.yome.dildiy.ui.ecommerce.ProductScreen.ProductViewModel
import com.yome.dildiy.ui.ecommerce.checkout.CheckoutViewModel
import com.yome.dildiy.ui.ecommerce.checkout.DeliveryVm
import com.yome.dildiy.ui.ecommerce.checkout.OrderVm
import com.yome.dildiy.ui.ecommerce.checkout.PaymentViewModel
import com.yome.dildiy.ui.ecommerce.createProduct.LoginVm
import com.yome.dildiy.ui.ecommerce.createProduct.UploadProductVm
import com.yome.dildiy.ui.ecommerce.orderScreen.SoldViewModel
import com.yome.dildiy.ui.ecommerce.productdetail.ProductDetailVm
import com.yome.dildiy.ui.ecommerce.profile.ProfileVm
import com.yome.dildiy.ui.ecommerce.search.SearchViewModel
import com.yome.dildiy.ui.register.RegisterVm
import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun appModule() = listOf(provideHttpClientModule, provideRepositoryModule, provideViewModelModule)
val provideHttpClientModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
        }
    }
}
val provideRepositoryModule = module {
    single<ProductRepository> { ProductRepository(get()) }
    single<SearchRepository> { SearchRepository(get()) }
    single<ProductDetailRepository> { ProductDetailRepository(get()) }
    single<UploadProductRepository> { UploadProductRepository(get()) }
    single<LoginRepository> { LoginRepository(get()) }
    single<RegisterRepository> { RegisterRepository(get()) }
    single { ShoppingCartRepository(get()) }
    single { GetCartRepository(get()) }
    single{ OrderRepository(get()) }
    single{ PaymentRepository(get()) }
    single{ ProfileRepository(get()) }
    single{ DeliveryRepository(get()) }
    single{ OrderRepository(get()) }
    single{ SoldRepository(get()) }

}

val provideViewModelModule = module {
    single {
        UploadProductVm(get())
    }
    single {
        LoginVm(get())
    }
    single {
        ProductDetailVm(get())
    }
    single {
        SearchViewModel(get())
    }
    single {
        ProductViewModel(get())
    }
    single {
        RegisterVm(get())
    }
    single {
        ShoppingCartViewModel(get(), get())
    }
    single {
        CheckoutViewModel(get())
    }
    single {
        PaymentViewModel(get())
    }
    single {
        ProfileVm(get())
    }

    single {
        DeliveryVm(get())
    }

    single {
        OrderVm(get())
    }
    single {
        SoldViewModel(get())
    }

}