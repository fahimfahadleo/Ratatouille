package com.horoftech.ratatouille.viewModels;

import static com.horoftech.ratatouille.utils.Configurations.ONLINE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.PLAYER1;
import static com.horoftech.ratatouille.utils.Configurations.REFERENCE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.RETRIVE;

import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.horoftech.ratatouille.R;
import com.horoftech.ratatouille.models.ProfileModel;
import com.horoftech.ratatouille.utils.Configurations;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragmentViewModel extends ViewModel {
    public MutableLiveData<Point>map= new MutableLiveData<>();
    ArrayList<Integer>player1 = new ArrayList<>();
    ArrayList<Integer>player2 = new ArrayList<>();
    String myPlayerID = PLAYER1;
    ArrayList<ArrayList<Integer>>winningPositions = new ArrayList<>();
    public MutableLiveData<String>winner = new MutableLiveData<>();
    public MutableLiveData<String>draw = new MutableLiveData<>();
    public MutableLiveData<String>action = new MutableLiveData<>();
    public static final String ACTION_ADD = "add_people";
    public static final String ACTION_COPY = "copy";
    public static final String ACTION_DRAW = "draw";
    public static final String RESET = "reset";
    ArrayList<Integer>characters =new ArrayList<>();
    String roomID;
    boolean isOnline = false;

    public void setMyPlayerID(String id){
        this.myPlayerID = id;
    }
    public String getMyPlayerID(){
        return myPlayerID;
    }

    public void setIsOnline(boolean b){
        this.isOnline = b;
    }
    public boolean getIsOnline(){
        return isOnline;
    }
    public void setRoomID(String d){
        this.roomID = d;
    }
    public String getRoomID(){
        return roomID;
    }


    public boolean isEven = true;
    void changePosition(){
        isEven = !isEven;
    }
    void setForTextView(int tag){
        if(isEven){
            map.setValue(new Point("o",tag));
            player1.add(tag);
        }else {
            map.setValue(new Point("x",tag));
            player2.add(tag);
        }
        changePosition();
    }

    public void onAction(View vi){
        int id = vi.getId();
        if(id == R.id.add){
            action.setValue(ACTION_ADD);
        }else if(id == R.id.reference){
            action.setValue(ACTION_COPY);
        }else if(id ==R.id.reset){
            action.setValue(RESET);
        }
    }

    public void onClick(View vi){
        int tag = Integer.parseInt((String)vi.getTag());
        characters.add(tag);
        if(!player2.contains(tag) && !player1.contains(tag)){
            setForTextView(tag);
            if(getIsOnline()){
                FirebaseDatabase.getInstance().getReference().child(Configurations.ROOMS).child(getRoomID()).setValue(myPlayerID+"_"+tag);
            }
        }

        checkWin();
    }

    private void checkWin() {
        if(characters.size()== 10){
            StringBuilder s = new StringBuilder();
            for(int i:characters){
                s.append(i);
            }
            FirebaseDatabase.getInstance().getReference().child(REFERENCE_USERS).child(s.toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        ProfileModel profileModel = snapshot.getValue(ProfileModel.class);
                        if(profileModel!=null){
                            FirebaseDatabase.getInstance().getReference().child(ONLINE_USERS).child(profileModel.getUniqueID()).setValue(RETRIVE);
                        }
                    }else {
                        characters.clear();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        for(ArrayList<Integer> i:winningPositions){
            if(player1.containsAll(i)){
                winner.setValue("1_"+winningPositions.indexOf(i));
                return;
            }
            if(player2.containsAll(i)){
                winner.setValue("2_"+winningPositions.indexOf(i));
                return;
            }
        }

        if(player2.size()+player1.size() == 9){
            draw.setValue(ACTION_DRAW);
        }
    }

    public void reset(){
        player2.clear();
        player1.clear();
    }

    public void init() {
        ArrayList<Integer>winningPosition1 =new ArrayList<>();
        winningPosition1.add(1);
        winningPosition1.add(2);
        winningPosition1.add(3);
        ArrayList<Integer>winningPosition2 =new ArrayList<>();
        winningPosition2.add(4);
        winningPosition2.add(5);
        winningPosition2.add(6);
        ArrayList<Integer>winningPosition3 =new ArrayList<>();
        winningPosition3.add(7);
        winningPosition3.add(8);
        winningPosition3.add(9);
        ArrayList<Integer>winningPosition4 =new ArrayList<>();
        winningPosition4.add(1);
        winningPosition4.add(4);
        winningPosition4.add(6);
        ArrayList<Integer>winningPosition5 =new ArrayList<>();
        winningPosition5.add(2);
        winningPosition5.add(5);
        winningPosition5.add(8);
        ArrayList<Integer>winningPosition6 =new ArrayList<>();
        winningPosition6.add(3);
        winningPosition6.add(6);
        winningPosition6.add(9);
        ArrayList<Integer>winningPosition7 =new ArrayList<>();
        winningPosition7.add(1);
        winningPosition7.add(5);
        winningPosition7.add(9);
        ArrayList<Integer>winningPosition8 =new ArrayList<>();
        winningPosition8.add(3);
        winningPosition8.add(5);
        winningPosition8.add(7);
        winningPositions.add(winningPosition1);
        winningPositions.add(winningPosition2);
        winningPositions.add(winningPosition3);
        winningPositions.add(winningPosition4);
        winningPositions.add(winningPosition5);
        winningPositions.add(winningPosition6);
        winningPositions.add(winningPosition7);
        winningPositions.add(winningPosition8);
    }
    public static class Point {
        String data;
        Integer tag;
        public Point(String data,Integer tag){
            this.data = data;
            this.tag = tag;
        }

        public String getData() {
            return data;
        }
        public Integer getTag() {
            return tag;
        }
    }
}
