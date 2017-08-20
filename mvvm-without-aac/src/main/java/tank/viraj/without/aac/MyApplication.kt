package tank.viraj.without.aac

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }
}