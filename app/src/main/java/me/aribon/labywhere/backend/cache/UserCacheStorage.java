package me.aribon.labywhere.backend.cache;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;

import me.aribon.labywhere.LabywhereApplication;
import me.aribon.labywhere.backend.model.User;
import me.aribon.labywhere.utils.FileUtils;

/**
 * Created by aribon on 26/05/2016.
 */
public class UserCacheStorage extends AbsCacheStorage<String, User> {

        private static final String TAG = UserCacheStorage.class.getSimpleName();

        private static final String DEFAULT_FILE_NAME = "user";

        private static UserCacheStorage instance = null;

        private File cacheDir;

        public static UserCacheStorage getInstance() {
            if (instance == null)
                instance = new UserCacheStorage();
            return instance;
        }

        private UserCacheStorage() {
            cacheDir = LabywhereApplication.getContext().getCacheDir();
        }

        @Override
        public User get(@NonNull String key) {
            File userFile = buildFile(key);
            String fileContent = FileUtils.readFileContent(userFile);
            Gson gson = new Gson();
            User user = gson.fromJson(fileContent, User.class);
            return user;
        }

        @Override
        public User getAll() {
            File userFile = buildFile();
            String fileContent = FileUtils.readFileContent(userFile);
            Gson gson = new Gson();
            User user = gson.fromJson(fileContent, User.class);
            return user;
        }

        @Override
        public void put(@NonNull String key, @NonNull User user) {
            File userFile = buildFile(key);
            if (!isCached(key)) {
                Gson gson = new Gson();
                String userJson = gson.toJson(user, User.class);
//                Log.d(TAG, "put: " + userJson);
                FileUtils.writeToFile(userFile, userJson);
                setLastCacheUpdateTimeMillis();
            }
        }

        @Override
        public boolean isCached(@NonNull String key) {
            File userFile = buildFile(key);
            return FileUtils.exists(userFile);
        }

        @Override
        public boolean isExpired() {
            long currentTime = System.currentTimeMillis();
            long lastUpdateTime = this.getLastCacheUpdateTimeMillis();

            boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME_MILLIS);

            if (expired)
                deleteAll();

            return false;
        }

        @Override
        public boolean delete(@NonNull String key) {
            File userFile = buildFile(key);
            return userFile.delete();
        }

        @Override
        public boolean deleteAll() {
            File userFile = buildFile();
            return userFile.delete();
        }

        private File buildFile(@NonNull String key) {
            return FileUtils.buildFile(cacheDir, File.separator, DEFAULT_FILE_NAME, key);
        }

        private File buildFile() {
            return FileUtils.buildFile(cacheDir, File.separator, DEFAULT_FILE_NAME);
        }
}