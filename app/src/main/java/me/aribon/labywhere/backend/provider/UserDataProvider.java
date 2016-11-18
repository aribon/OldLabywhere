package me.aribon.labywhere.backend.provider;

import android.support.annotation.NonNull;
import android.util.Log;

import me.aribon.labywhere.backend.cache.UserCacheStorage;
import me.aribon.labywhere.backend.model.User;
import me.aribon.labywhere.backend.network.storage.UserNetworkStorage;
import me.aribon.labywhere.backend.preferences.AuthPreferences;
import rx.Observable;

/**
 * Created by aribon from Insign Mobility
 * on 11/10/2016
 */
public class UserDataProvider extends AbsDataProvider {

    private static final String TAG = UserDataProvider.class.getSimpleName();

    private static UserDataProvider ourInstance = new UserDataProvider();

    public static UserDataProvider getInstance() {
        if (ourInstance == null)
            ourInstance = new UserDataProvider();
        return ourInstance;
    }

    private UserDataProvider() {
        super();
    }

    @Override
    Observable.Transformer<User, User> clearDataIfStale(int id) {
        return userObservable -> userObservable.doOnNext(user -> {
            if (user != null && !user.isUpToDate()) {
                UserCacheStorage.getInstance().delete(String.valueOf(id));
            }
        });
    }

    //Observable from Cache
    private Observable<User> getUserFromCache(int id) {
        return UserCacheStorage.getInstance().get(String.valueOf(id))
                .compose(logSource("CACHE"))
                .compose(clearDataIfStale(id));
    }

    //Observable from Network
    private Observable<User> getUserFromNetwork(int id) {
        return UserNetworkStorage.getInstance(AuthPreferences.getAuthToken()).get(id)
                .compose(logSource("NETWORK"));
    }

    //Observable from Network with save on Cache
    private Observable<User> getUserFromNetworkWithSave(int id) {
        return UserNetworkStorage.getInstance(AuthPreferences.getAuthToken()).get(id)
                .doOnNext(this::saveUserOnCache)
                .compose(logSource("NETWORK"));
    }

    //Save on Cache
    private void saveUserOnCache(final User user) {
        Log.d(TAG, "saveUsersOnCache");
        if (user != null)
            UserCacheStorage.getInstance().put(String.valueOf(user.getId()), user);
    }

    //Final observable
    @NonNull
    public Observable<User> getUser(int id) {
        return Observable
                .concat(getUserFromCache(id), getUserFromNetworkWithSave(id))
                .takeFirst(user -> user != null && user.isUpToDate());
    }

    public Observable<User> getAccount() {
        return UserNetworkStorage.getInstance(AuthPreferences.getAuthToken()).getAccount();
    }
}
