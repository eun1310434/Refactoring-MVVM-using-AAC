package tank.viraj.without.aac

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LeakCanary.install(this)
        //Leak Canary : akCanary 는 Open Source Java library 로 메모리 릭을 검출
        InternetUtil.init(this)
    }
}