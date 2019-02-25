package poc.gcp.storage;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Entity;
import com.google.cloud.storage.Acl.Entity.Type;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.Channels;

public class App {

  private final static String PROJECT_ID = "kf-216303";

  private final static String BUCKET_NAME = "gcs-test-0001";

  public static void main(String[] args) {
//    createBucket();
    readFile();
//    givePermission();
  }

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

  private static void readFile() {
    // Instantiates a client
    Storage storage = StorageOptions.newBuilder()
        .setProjectId(PROJECT_ID)
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
