package tank.viraj.without.aac

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment.*

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyFragment : Fragment() {

    private val TAG = "MyFragment"
    private val myDataSource = MyDataSource()
    private lateinit var myViewModel: MyViewModel

    private val viewSubscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        myViewModel = MyViewModel(myDataSource)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh_view.isEnabled = false
    }

    override fun onStart() {
        super.onStart()

        viewSubscriptions.add(myViewModel.viewSubscription
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    text_view.text = it?.myData
                    refresh_view.isRefreshing = it?.loadingState ?: false
                    if (it?.shouldWaitForInternet ?: false) waitForInternet()
                }, { error -> Log.e(TAG, "Error", error) }))
    }

    override fun onStop() {
        InternetUtil.stopWaitForInternet()
        viewSubscriptions.clear()
        super.onStop()
    }

    override fun onDestroy() {
        myViewModel.cleanUp()
        super.onDestroy()
    }

    fun waitForInternet() {
        viewSubscriptions.add(InternetUtil.waitForInternet()
                .observeOn(Schedulers.computation())
                .subscribe({
                    status ->
                    if (status ?: false) {
                        InternetUtil.stopWaitForInternet()
                        myViewModel.getData()
                    }
                }))
    }
}