package com.example.thomaswilkinson.program5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

public class DrawView extends View {
    Paint black, other, myColor, myColor2;
    public Context myContext;
    public int board[][];
    int mheight = 0, mwidth = 0;
    int incr, playerNum;
    int leftside, rightside, boardwidth;
    int counter=0;
    String UserNickName = "TicTacAdvertise";
    String UserNickName2 = "TicTacDiscover";
    String ConnectedEndPointId;
    Button button;
    Boolean initiating = true;
    Boolean advertising = false;
    Boolean yourTurn = false;
    Boolean checking = true;
    Boolean gameOver = false;
    Boolean overWait = false;
    int selection = 0;

    Boolean playerX = false;
    public DrawView(Context context) {
        super(context);
        myContext = context;
        build();
    }

    public DrawView(Context context, AttributeSet atribset) {
        super(context, atribset);
        myContext = context;
        build();
    }

    public DrawView(Context context, AttributeSet atribset, int defStyle) {
        super(context, atribset, defStyle);
        myContext = context;
        build();
    }

    public void build() {
        myColor = new Paint();
        myColor.setColor(Color.RED);
        myColor.setStyle(Paint.Style.STROKE);
        myColor.setStrokeWidth(10);

        myColor2 = new Paint();
        myColor2.setColor(Color.BLUE);
        myColor2.setStyle(Paint.Style.STROKE);
        myColor2.setStrokeWidth(10);

        black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);

        other = new Paint();
        other.setStyle(Paint.Style.FILL);

        board = null;
        board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < 3; x++) {
                board[i][x] = Color.BLACK;
            }
        }
        if (mheight > 0) {
            setsizes();
        }
        Dialog dialog = null;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(myContext);
        builder.setTitle("Tic Tac Toe");
        builder.setMessage("Welcome!\nTake turns pressing boxes to play.\nPlayer X starts.\nPress Restart to restart the game. ")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clearboard();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    public void setsizes() {

        incr = mwidth / (5);
        leftside = incr - 1;
        rightside = incr * 9;
        boardwidth = incr * 3;
    }

    void startAdvertise(){
        Nearby.getConnectionsClient(getContext())
                .startAdvertising(
                        UserNickName,
                        MainActivity.ServiceId,
                        mConnectionLifecycleCallback,
                        new AdvertisingOptions(MainActivity.STRATEGY));
        advertising = true;
    }

    void startDiscovery(){
        Nearby.getConnectionsClient(getContext()). startDiscovery(
                MainActivity.ServiceId,
                new EndpointDiscoveryCallback() {
                    @Override public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        Nearby.getConnectionsClient(getContext())
                                .requestConnection(
                                        UserNickName,
                                        endpointId,
                                        mConnectionLifecycleCallback);
                    }
                    @Override
                    public void onEndpointLost(String endpointId) {
                    }
                },
                new DiscoveryOptions(MainActivity.STRATEGY));


    }

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Nearby.getConnectionsClient(getContext()).acceptConnection(endpointId, //mPayloadCallback);
                            new PayloadCallback() {
                                @Override
                                public void onPayloadReceived(String endpointId, Payload payload) {

                                    if (payload.getType() == Payload.Type.BYTES) {
                                            String stuff = new String(payload.asBytes());
                                            if(gameOver) {
                                                checking=true;
                                                if (advertising) {
                                                    if (stuff.equals("agree")) {
                                                        initiating = true;
                                                        gameOver = false;
                                                        Dialog dialog = null;
                                                        AlertDialog.Builder builder;
                                                        builder = new AlertDialog.Builder(myContext);
                                                        builder.setTitle("Tic Tac Toe");
                                                        builder.setMessage("Hello Server!\n Would you like to play as player X or O?")
                                                                .setCancelable(false)
                                                                .setPositiveButton("Player X", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        String data = "Player X";
                                                                        Payload payload = Payload.fromBytes(data.getBytes());
                                                                        Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                                        yourTurn = true;
                                                                        playerX = true;
                                                                        playerNum = 1;
                                                                    }
                                                                })
                                                                .setNegativeButton("Player O", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        String data = "Player O";
                                                                        Payload payload = Payload.fromBytes(data.getBytes());
                                                                        Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                                        playerNum = 2;
                                                                    }
                                                                });

                                                        dialog = builder.create();
                                                        dialog.show();
                                                    }
                                                    else{
                                                        System.exit(0);
                                                    }
                                                }
                                                else{
                                                if (stuff.equals("playagain")) {
                                                    Dialog dialog = null;
                                                    AlertDialog.Builder builder;
                                                    builder = new AlertDialog.Builder(myContext);
                                                    builder.setTitle("Tic Tac Toe");
                                                    builder.setMessage("The server wants to play again! Do you?")
                                                            .setCancelable(false)
                                                            .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    initiating = true;
                                                                    gameOver = false;
                                                                    String data = "agree";
                                                                    Payload payload = Payload.fromBytes(data.getBytes());
                                                                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                                }
                                                            })
                                                            .setNegativeButton("No!", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    String data = "disagree";
                                                                    Payload payload = Payload.fromBytes(data.getBytes());
                                                                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                                    System.exit(0);
                                                                }
                                                            });

                                                    dialog = builder.create();
                                                    dialog.show();
                                                }
                                                }
                                            }
                                            else{
                                            String data = checkStuff(stuff);
                                            if (!data.equals("play") && !data.equals("wait")) {

                                                if (initiating) {
                                                    if (!advertising) {
                                                        payload = Payload.fromBytes(data.getBytes());
                                                        Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                    }
                                                } else {
                                                    payload = Payload.fromBytes(data.getBytes());
                                                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                    if (data.equals("disagree")) System.exit(0);
                                                }
                                            }
                                            }


                                    } else if (payload.getType() == Payload.Type.FILE);
                                    else if (payload.getType() == Payload.Type.STREAM);
                                }

                                @Override
                                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate payloadTransferUpdate) {
                                }
                            });
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {

                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            Nearby.getConnectionsClient(getContext()).stopDiscovery();
                            ConnectedEndPointId = endpointId;
                            if(advertising)
                            {
                                Dialog dialog = null;
                                AlertDialog.Builder builder;
                                builder = new AlertDialog.Builder(myContext);
                                builder.setTitle("Tic Tac Toe");
                                builder.setMessage("Hello Server!\n Would you like to play as player X or O?")
                                        .setCancelable(false)
                                        .setPositiveButton("Player X", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String data = "Player X";
                                                Payload payload = Payload.fromBytes(data.getBytes());
                                                Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                yourTurn = true;
                                                playerX = true;
                                                playerNum = 1;
                                            }
                                        })
                                        .setNegativeButton("Player O", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String data = "Player O";
                                                Payload payload = Payload.fromBytes(data.getBytes());
                                                Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                                                playerNum = 2;
                                            }
                                        });

                                dialog = builder.create();
                                dialog.show();
                            }
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            System.exit(0);
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            System.exit(0);
                            break;
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    ConnectedEndPointId = "";  //need a remove if using a list.
                }
            };
    void clearboard() {
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < 3; x++) {
                board[i][x] = Color.BLACK;
            }
        }
        invalidate();
    }

    String checkStuff(String data){
        if(gameOver) {
                if(data.equals("exit")){
                    data = "agree";
                    Payload payload = Payload.fromBytes(data.getBytes());
                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                    System.exit(0);
                }
        }
        if(initiating) {
            if (!advertising) {
                if (data.equals("Player X")) {
                    initiating = false;
                    playerNum = 2;
                    return "agree";
                } else if (data.equals("Player O")) {
                    initiating = false;
                    playerX = true;
                    yourTurn = true;
                    playerNum = 1;
                    PlayTurn();
                    String temp = "agree";
                    Payload payload = Payload.fromBytes(temp.getBytes());
                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                    return "play";
                }
                else if (data.equals("exit")){
                    String temp = "agree";
                    Payload payload = Payload.fromBytes(temp.getBytes());
                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                    System.exit(0);
                }
            }
            else{
                if (data.equals("agree")){
                    if(playerX){
                        initiating = false;
                        PlayTurn();
                        return "play";
                    }
                    else{
                        initiating = false;
                        Wait();
                        return"wait";
                    }
                }
                else if (data.equals("disagree")){
                    System.exit(0);
                }
            }
        }
        switch(data){
            case "1":
                if(board[0][2] == Color.BLACK) {
                    if(playerNum == 1){
                        board[0][2] = 2;
                    }else board[0][2] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else {return "disagree";}
            case "2":
                if(board[1][2] == Color.BLACK) {
                    if(playerNum == 1){
                        board[1][2] = 2;
                    }else board[1][2] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "3":
                if(board[2][2] == Color.BLACK) {
                    if(playerNum == 1){
                        board[2][2] = 2;
                    }else board[2][2] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "4":
                if(board[0][1] == Color.BLACK) {
                    if(playerNum == 1){
                        board[0][1] = 2;
                    }else board[0][1] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "5":
                if(board[1][1] == Color.BLACK) {
                    if(playerNum == 1){
                        board[1][1] = 2;
                    }else board[1][1] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "6":
                if(board[2][1] == Color.BLACK) {
                    if(playerNum == 1){
                        board[2][1] = 2;
                    }else board[2][1] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "7":
                if(board[0][0] == Color.BLACK) {
                    if(playerNum == 1){
                        board[0][0] = 2;
                    }else board[0][0] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "8":
                if(board[1][0] == Color.BLACK) {
                    if(playerNum == 1){
                        board[1][0] = 2;
                    }else board[1][0] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "9":
                if(board[2][0] == Color.BLACK) {
                    if(playerNum == 1){
                        board[2][0] = 2;
                    }else board[2][0] = 1;
                    invalidate();
                    counter++;
                    return "agree";
                }
                else return "disagree";
            case "nowinner":
                if(checkWinner() == 0) {
                    yourTurn = true;
                    checking = true;
                    PlayTurn();
                    return "agree";
                }
                else return "disagree";
            case "winner":
                int temp = checkWinner();
                if(temp == 1 || temp == 2)return "agree";
                else return "disagree";
            case "tie":
                if(checkWinner() == 3) return "agree";
                else return "disagree";
            case "agree":
                if(checking) {
                    if (!yourTurn) {
                        int temp1 = checkWinner();
                        if (temp1 == 0) {
                            checking = false;
                            return "nowinner";
                        }
                        else if (temp1 == 1 || temp1 == 2) {
                            checking = false;
                            return "winner";
                        }
                        else if (temp1 == 3) {
                            checking = false;
                            return "tie";
                        }
                    }
                    else {
                        PlayTurn();
                    }
                }
                 else return "wait";

        }
        System.exit(0);
        return "";
    }

    void PlayTurn(){
        Dialog dialog = null;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(myContext);
        builder.setTitle("Tic Tac Toe");
        builder.setMessage("It is your turn! Select your move!")
                .setCancelable(false)
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    void Wait(){
        Dialog dialog = null;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(myContext);
        builder.setTitle("Tic Tac Toe");
        builder.setMessage("It is not your turn! Wait for the other player to select a play!")
                .setCancelable(false)
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = incr;
        int y = incr;
        canvas.drawColor(Color.WHITE);
        for (int yi = 0; yi < 3; yi++) {
            for (int xi = 0; xi < 3; xi++) {
                canvas.drawRect(x, y, x + incr, y + incr, black);
                if (board[xi][yi] == 2) {
                    canvas.drawCircle(x+100, y+100, 80,myColor2);
                }
                else if (board[xi][yi] == 1)
                {
                    canvas.drawLine(x+20,y+20, x+180, y+180, myColor);
                    canvas.drawLine(x+20,y+180,x+180, y+20, myColor);
                }
                x += incr;
            }
            x = incr;
            y += incr;
        }
    }

    boolean where(int x, int y) {
        int cx = -1, cy = -1;
        if (y >= leftside && y < rightside &&
                x >= leftside && x < rightside) {
            y -= incr;
            x -= incr;
            cx = x / incr;
            cy = y / incr;
            if (cx < 3 && cy < 3) {
                if (board[cx][cy] == Color.BLACK) {
                    board[cx][cy] = playerNum;
                    if(cx == 0 && cy ==0) selection = 7;
                    if(cx == 1 && cy ==0) selection = 8;
                    if(cx == 2 && cy ==0) selection = 9;
                    if(cx == 0 && cy ==1) selection = 4;
                    if(cx == 1 && cy ==1) selection = 5;
                    if(cx == 2 && cy ==1) selection = 6;
                    if(cx == 0 && cy ==2) selection = 1;
                    if(cx == 1 && cy ==2) selection = 2;
                    if(cx == 2 && cy ==2) selection = 3;
                    String data = "" + selection;
                    Payload payload = Payload.fromBytes(data.getBytes());
                    Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                    Wait();

                    counter++;
                    yourTurn = false;
                }
            }
            return true;
        }
        return false;
    }

    int checkWinner(){
        if((board[0][0] ==1 && board[0][1]==1 && board[0][2]==1)||(board[0][0] ==2 && board[0][1]==2 && board[0][2]==2)){
            declareWinner(board[0][0]);
            return board[0][0];
        }
        if((board[0][0] ==1 && board[1][0]==1 && board[2][0]==1)||(board[0][0] ==2 && board[1][0]==2 && board[2][0]==2)){
            declareWinner(board[0][0]);
            return board[0][0];
        }
        if((board[0][0] ==1 && board[1][1]==1 && board[2][2]==1)||(board[0][0] ==2 && board[1][1]==2 && board[2][2]==2)){
            declareWinner(board[0][0]);
            return board[0][0];
        }
        if((board[2][0] ==1 && board[2][1]==1 && board[2][2]==1)||(board[2][0] ==2 && board[2][1]==2 && board[2][2]==2)){
            declareWinner(board[2][0]);
            return board[2][0];
        }
        if((board[0][2] ==1 && board[1][2]==1 && board[2][2]==1)||(board[0][2] ==2 && board[1][2]==2 && board[2][2]==2)){
            declareWinner(board[0][2]);
            return board[0][2];
        }
        if((board[2][0] ==1 && board[1][1]==1 && board[0][2]==1)||(board[2][0] ==2 && board[1][1]==2 && board[0][2]==2)){
            declareWinner(board[2][0]);
            return board[2][0];
        }
        if((board[0][1] ==1 && board[1][1]==1 && board[2][1]==1)||(board[0][1] ==2 && board[1][1]==2 && board[2][1]==2)){
            declareWinner(board[0][1]);
            return board[0][1];
        }
        if((board[1][0] ==1 && board[1][1]==1 && board[1][2]==1)||(board[1][0] ==2 && board[1][1]==2 && board[1][2]==2)){
            declareWinner(board[1][0]);
            return board[1][0];
        }
        else if(counter == 9)
        {
            if(advertising){
                counter = 0;
                Dialog dialog = null;
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(myContext);

                builder.setMessage("Tie game!\n\nPlay again?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearboard();
                                restartGame();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String data = "exit";
                        Payload payload = Payload.fromBytes(data.getBytes());
                        Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                dialog = builder.create();
                dialog.show();

                return 3;
            }
            else{
                counter = 0;
                Dialog dialog = null;
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(myContext);

                builder.setMessage("Tie game!\n\nWait for server!")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearboard();
                                yourTurn =false;
                            }
                });
                dialog = builder.create();
                dialog.show();
                gameOver = true;

                return 3;
            }
        }
        return 0;
    }

    void restartGame(){
        gameOver = true;
        if(advertising){
            String data = "playagain";
            Payload payload = Payload.fromBytes(data.getBytes());
            Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
        }
    }

    void declareWinner(int player){
        Dialog dialog = null;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(myContext);
        if(advertising) {
            if (player == playerNum) {
                builder.setMessage("You win!\n\nPlay again?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                yourTurn = false;
                                clearboard();
                                restartGame();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        String data = "exit";
                        Payload payload = Payload.fromBytes(data.getBytes());
                        Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                        System.exit(0);
                    }
                });
            } else {
                builder.setMessage("The other player wins!\n\nPlay again?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                yourTurn = false;
                                clearboard();
                                restartGame();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        String data = "exit";
                        Payload payload = Payload.fromBytes(data.getBytes());
                        Nearby.getConnectionsClient(getContext()).sendPayload(ConnectedEndPointId, payload);
                        System.exit(0);
                    }
                });
            }
            dialog = builder.create();
            dialog.show();
            counter = 0;
        }
        else{
            if (player == playerNum) {
                builder.setMessage("You win!\n\nWait for server.")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearboard();
                                yourTurn = false;
                                gameOver = true;
                            }
                });
            } else {
                builder.setMessage("The other player wins!\n\nWait for server.")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                clearboard();
                            }
                });
            }
            dialog = builder.create();
            dialog.show();
            yourTurn = false;
            gameOver = true;
            counter = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!yourTurn)return true;
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                where(x, y);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mwidth = getMeasuredWidth();
        mheight = getMeasuredHeight();
        if (mheight > 0 && mwidth > mheight) {
            mwidth = mheight;
        } else if (mheight == 0) {
            mheight = mwidth;
        }
        setsizes();
        setMeasuredDimension(mwidth, mheight);
    }
}
