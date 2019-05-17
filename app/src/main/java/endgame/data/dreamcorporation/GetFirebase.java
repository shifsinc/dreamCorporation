package endgame.data.dreamcorporation;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GetFirebase {

//  private FirebaseAuth mAuth = FirebaseAuth.getInstance();
  private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
  protected static DatabaseReference adminRef = mDatabase.getReference("admin");
  protected static DatabaseReference usersRef = mDatabase.getReference("users");
  protected static DatabaseReference transRef = mDatabase.getReference("transactions");

  private static String adminUid;
  private static double fee;
  private static ArrayList<String> usersUid = new ArrayList<>();
  private static ArrayList<Users> users = new ArrayList<>();
  private static ArrayList<String> transactionId = new ArrayList<>();
  private static ArrayList<Users> transactions = new ArrayList<>();

  public static void prepareFirebase() { mDatabase.setPersistenceEnabled(true); }

  public static void getFirebase() {
    fetchAdmin();
    fetchUsers();
    updateBalance();
  }

  public static void fetchAdmin() {
    adminRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        adminUid = dataSnapshot.child("uid").getValue().toString();
        fee = Double.valueOf(dataSnapshot.child("fee").getValue().toString());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public static String getAdminUid() {
    return adminUid;
  }

  public static double getFee() {
    return fee;
  }



  public static void fetchUsers() {
//    Log.e("At", "fetchUsers outside");
    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//        Log.e("At", "fetchUsers inside");
        HashMap<String, HashMap<String, Object>> tempUsers = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

        for (String tempUser : tempUsers.keySet()) {
//          Log.e("added userId", tempUser);
          usersUid.add(tempUser);
        }

        for (HashMap<String, Object> tempUser : tempUsers.values()) {
          users.add(new Users(((String) tempUser.get("fN")), Double.valueOf(String.valueOf(tempUser.get("b")))));

          if (tempUser.containsKey("upId")) {
            users.get(users.size() - 1).setUplineUid(String.valueOf(tempUser.get("upId")));
          }

          if (tempUser.containsKey("dwId")) {
            ArrayList<String> tempDwArray = new ArrayList<>();

            for (String tempDwId: ((ArrayList<String>) tempUser.get("dwId"))) {
              tempDwArray.add(tempDwId);
            }

            users.get(users.size() - 1).setDownlineUid(tempDwArray);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public static void fetchTransactions() {
//    Log.e("At", "fetchUsers outside");
    transRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
      HashMap<String, HashMap<String, Object>> tempTrans = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

        for (String tempTran : tempTrans.keySet()) {
//          Log.e("added userId", tempTran);
          transactionId.add(tempTran);
        }

        for (HashMap<String, Object> tempUser : tempTrans.values()) {
          users.add(new Users(((String) tempUser.get("fN")), Double.valueOf(String.valueOf(tempUser.get("b")))));

          if (tempUser.containsKey("upId")) {
            users.get(users.size() - 1).setUplineUid(String.valueOf(tempUser.get("upId")));
          }

          if (tempUser.containsKey("dwId")) {
            ArrayList<String> tempDwArray = new ArrayList<>();

            for (String tempDwId: ((ArrayList<String>) tempUser.get("dwId"))) {
              tempDwArray.add(tempDwId);
            }

            users.get(users.size() - 1).setDownlineUid(tempDwArray);
          }

          if (tempUser.containsKey("dwId")) {
            ArrayList<String> tempDwArray = new ArrayList<>();

            for (String tempDwId: ((ArrayList<String>) tempUser.get("dwId"))) {
              tempDwArray.add(tempDwId);
            }

            users.get(users.size() - 1).setDownlineUid(tempDwArray);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public static Users getUsers(String uid) {
//    Log.e("uid", uid);
//    Log.e("usersUid", usersUid.toString());
    int tempPosition = usersUid.indexOf(uid);
//    Log.e("usersUid", usersUid.toString());
//    Log.e("tempPos", String.valueOf(tempPosition));
    return users.get(tempPosition);
  }

  public static void updateBalance() {
    usersRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.e("At", "fetchUsers inside");
        HashMap<String, HashMap<String, Object>> tempUsers = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

        int i = 0;

        for (HashMap<String, Object> tempUser : tempUsers.values()) {
          users.get(i++).setBalance(Double.valueOf(tempUser.get("b").toString()));
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public static void addBalance(final String uid[], final double amount[]) {
    Log.e("uid length", String.valueOf(uid.length));

    usersRef.child(uid[0]).child("b").addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        double tempBalance = Double.parseDouble(dataSnapshot.getValue().toString());

        tempBalance += amount[0];

        usersRef.child(uid[0]).child("b").setValue(tempBalance);

        if (uid.length > 1) {
          addBalance(Arrays.copyOfRange(uid, 1, uid.length), Arrays.copyOfRange(amount, 1, amount.length));
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
      }
    });
  }
}