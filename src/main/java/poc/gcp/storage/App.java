package poc.gcp.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.CharStreams;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

public class App {

  private final static String PROJECT_ID = "kf-216303";

  private final static String BUCKET_NAME = "gcs-test-0001";

  public static void main(String[] args) {
//    createBucket();
    readFile();
//    givePermission();
  }

  // create bucket using access key of first service account
  private static void createBucket() {
    // Instantiates a client
    Storage storage = StorageOptions.newBuilder()
        .setProjectId(PROJECT_ID)
        .build()
        .getService();

    // The name for the new bucket
    String bucketName = BUCKET_NAME;

    // Creates the new bucket
    Bucket bucket = storage.create(BucketInfo.of(bucketName));

  }

  // Read using access key of second service account
  private static void readFile() {

    String credentialFile = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

//    GoogleCredentials credentials = null;
//    File credentialsPath = new File(credentialFile);
//    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
//      credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }

    GoogleCredentials credentials = null;
    File credentialsPath = new File(credentialFile);
    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {

      // Convert to String
      String text = null;
      try (final Reader reader = new InputStreamReader(serviceAccountStream)) {
        text = CharStreams.toString(reader);
      }

      System.out.println(text);

      // String to InputStream
      InputStream is = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));


      // Create credential
      credentials = ServiceAccountCredentials.fromStream(is);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Instantiates a client
    Storage storage = StorageOptions.newBuilder()
        .setProjectId(PROJECT_ID)
        .setCredentials(credentials)
        .build()
        .getService();

    // The name for the new bucket
    String bucketName = BUCKET_NAME;

    // Read file
    String srcFilename = "CHANGELOG.md";
    Blob blob = storage.get(BlobId.of(bucketName, srcFilename));
    ReadChannel reader = blob.reader();
    BufferedReader bfr = new BufferedReader(new InputStreamReader(Channels.newInputStream(reader)));
    bfr.lines().forEach(s -> System.out.println(s));
  }

  // give permission using access key of first service account
  private static void givePermission() {
    // Instantiates a client
    Storage storage = StorageOptions.newBuilder()
        .setProjectId(PROJECT_ID)
        .build()
        .getService();

    // The name for the new bucket
    String bucketName = BUCKET_NAME;

    String srcFilename = "CHANGELOG.md";

    Blob blob = storage.get(BlobId.of(bucketName, srcFilename));
    BlobId blobId = blob.getBlobId();

    String email = "gcs-002-test@kf-216303.iam.gserviceaccount.com";
    User user = new User(email);

    Acl acl = Acl.newBuilder(user, Role.READER).build();
    Acl acl1 = storage.createAcl(blobId, acl);
  }

}
