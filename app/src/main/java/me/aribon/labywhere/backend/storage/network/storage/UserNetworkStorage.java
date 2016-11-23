package me.aribon.labywhere.backend.storage.network.storage;

import android.util.Log;

import java.util.List;

import me.aribon.labywhere.backend.model.User;
import me.aribon.labywhere.backend.storage.network.response.UserListResponse;
import me.aribon.labywhere.backend.storage.network.response.UserResponse;
import me.aribon.labywhere.backend.storage.network.service.UserService;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by aribon on 16/05/2016.
 */
public class UserNetworkStorage extends AbsNetworkStorage<User> {

    private static final String TAG = UserNetworkStorage.class.getSimpleName();

    private static UserNetworkStorage instance;

    private static String sToken;

    public static UserNetworkStorage getInstance(String token) {
        sToken = token;
        if (instance == null)
            instance = new UserNetworkStorage();
        return instance;
    }

    private UserNetworkStorage() {
    }

    public Observable<User> getAccount() {
        return createService(UserService.class, sToken).getAccount()
                .subscribeOn(Schedulers.io())
                .flatMap(
                        new Func1<UserResponse, Observable<User>>() {
                            @Override
                            public Observable<User> call(UserResponse userResponse) {
                                if (userResponse != null)
                                    Log.d(TAG, "call: ");
                                return Observable.just(userResponse.getUser());
                            }
                        }
                );
    }

    @Override
    public Observable<User> get(int id) {
        return createService(UserService.class, sToken).getUser(id)
                .subscribeOn(Schedulers.io())
                .flatMap(
                        new Func1<UserResponse, Observable<User>>() {
                            @Override
                            public Observable<User> call(UserResponse userResponse) {
                                return Observable.just(userResponse.getUser());
                            }
                        }
                );
    }

    @Override
    public Observable<List<User>> getAll() {
        return createService(UserService.class, sToken).getAllUsers()
                .subscribeOn(Schedulers.io())
                .flatMap(
                        new Func1<UserListResponse, Observable<List<User>>>() {
                            @Override
                            public Observable<List<User>> call(UserListResponse userListResponse) {
                                return Observable.just(userListResponse.getUsers());
                            }
                        }
                );
    }
}
