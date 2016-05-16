package me.aribon.labywhere.backend.webservice.service;

import me.aribon.labywhere.backend.webservice.WebServiceManager;
import me.aribon.labywhere.backend.webservice.api.UserApi;
import me.aribon.labywhere.backend.webservice.response.UserListResponse;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by aribon on 16/05/2016.
 */
public class UserService {

    private static final String TAG = UserService.class.getSimpleName();

    public static Subscription getAllUsers(String token, Observer<UserListResponse> observer) {
        return WebServiceManager.createService(UserApi.class, token).getAllUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}