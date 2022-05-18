package com.finsol.tech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.finsol.tech.rabbitmq.MySingletonViewModel
import java.lang.reflect.InvocationTargetException


class SingletonNameViewModelFactory(private val myViewModel: MySingletonViewModel) :
    ViewModelProvider.NewInstanceFactory() {
    private val mFactory: MutableMap<Class<out ViewModel>, ViewModel?> = HashMap()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        mFactory[modelClass] = myViewModel
        if (MySingletonViewModel::class.java.isAssignableFrom(modelClass)) {
            var shareVM: MySingletonViewModel? = null
            if (mFactory.containsKey(modelClass)) {
                shareVM = mFactory[modelClass] as MySingletonViewModel?
            } else {
                shareVM = try {
                    modelClass.getConstructor(Runnable::class.java).newInstance(
                        Runnable { mFactory.remove(modelClass) }) as MySingletonViewModel
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of $modelClass", e)
                }
                mFactory[modelClass] = shareVM
            }
            return shareVM as T
        }
        return super.create(modelClass)
    }


}