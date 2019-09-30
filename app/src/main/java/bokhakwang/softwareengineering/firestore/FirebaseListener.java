package bokhakwang.softwareengineering.firestore;

public interface FirebaseListener<T> {
    void onComplete(T result);
}
