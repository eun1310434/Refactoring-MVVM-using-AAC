package tank.viraj.without.aac

import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Viraj Tank, 15-08-2017.
 */
class MyViewModel(val myDataSource: MyDataSource) {

    private val dataSubscriptions = CompositeDisposable()
    val viewSubscription = BehaviorSubject.create<MyViewState>()!!

    private var myViewState = MyViewState()

    init {
        getData()
    }

    fun getData() {
        dataSubscriptions.add(Maybe.concat(
                myDataSource.getDataFromFakeDatabase(),
                myDataSource.getDataFromFakeNetwork())
                .firstElement()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .doOnSubscribe {
                    myViewState = myViewState.copy(loadingState = true,
                            shouldWaitForInternet = false)
                    viewSubscription.onNext(myViewState)
                }
                .subscribe({ data ->
                    myViewState = myViewState.copy(myData = data,
                            loadingState = false,
                            shouldWaitForInternet = false)
                    viewSubscription.onNext(myViewState)
                }) { error ->
                    if (error is NoInternetException) {
                        myViewState = myViewState.copy(myData = "No Internet",
                                loadingState = false,
                                shouldWaitForInternet = true)
                    } else {
                        myViewState = myViewState.copy(myData = "Generic Error",
                                loadingState = false,
                                shouldWaitForInternet = false)
                    }
                    viewSubscription.onNext(myViewState)
                })
    }

    fun cleanUp() {
        dataSubscriptions.dispose()
    }
}