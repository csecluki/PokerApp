package com.example.pokerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnSend, btnRoom;
    private ListView listViewPeers;
    private EditText editTextMessage;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private Handler handler;
    private boolean isClient = true;
    private Server server;
    private Client client;

    private final ArrayList<WifiP2pDevice> peerArrayList = new ArrayList<>();
    private WifiP2pDevice[] deviceArray;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartDiscovering = findViewById(R.id.btnStartDiscovering);
        Button btnStopDiscovering = findViewById(R.id.btnStopDiscovering);
        Button btnRequest = findViewById(R.id.btnRequest);
        btnRoom = findViewById(R.id.btnRoom);
        btnRoom.setEnabled(false);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setEnabled(false);
        listViewPeers = findViewById(R.id.listViewPeers);
//        listViewConnectedDevices = findViewById(R.id.listViewConnectedDevices);
        editTextMessage = findViewById(R.id.editTextMessage);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WifiDirectBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        handler = new Handler();

        hasPermissions(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
        });

        btnStartDiscovering.setOnClickListener(view -> manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Searching for devices...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Failed to search for devices", Toast.LENGTH_SHORT).show();
            }
        }));

        btnStopDiscovering.setOnClickListener(view -> manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Searching for devices stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Failed to stop searching for devices", Toast.LENGTH_SHORT).show();
            }
        }));

        btnRequest.setOnClickListener(view -> manager.requestGroupInfo(channel, group -> {
            if (group == null) {
                Toast.makeText(getApplicationContext(), "You are not in any group", Toast.LENGTH_SHORT).show();
            } else if (group.isGroupOwner()) {
                Toast.makeText(getApplicationContext(), "You are owner of " + (group.getClientList().size() + 1) + " devices group", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "You are connected to " + group.getOwner().deviceName, Toast.LENGTH_SHORT).show();
            }
        }));

        listViewPeers.setOnItemClickListener((adapterView, view, i, l) -> {
            WifiP2pDevice device = deviceArray[i];
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Connected with: " + device.deviceName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "Cannot connect with: " + device.deviceName, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRoom.setOnClickListener(view -> {
            try {
                server.broadcastMessage("/goToRoom");
            } catch (NullPointerException e) {
                assert true;
            } finally {
                launchGameRoomActivity();
            }
        });

        btnSend.setOnClickListener(view -> {
            if (isClient) {
                client.sendMessage(editTextMessage.getText().toString());
            } else {
                server.broadcastMessage(editTextMessage.getText().toString());
            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.getDeviceList().equals(peerArrayList)) {
                peerArrayList.clear();
                peerArrayList.addAll(wifiP2pDeviceList.getDeviceList());

                String[] deviceNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];

                int i = 0;
                for (WifiP2pDevice device: wifiP2pDeviceList.getDeviceList()) {
                    deviceNameArray[i] = device.deviceName;
                    deviceArray[i] = device;
                    i++;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        deviceNameArray
                );
                listViewPeers.setAdapter(arrayAdapter);
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed) {
                if (server == null && client == null) {
                    Toast.makeText(getApplicationContext(), "Im here", Toast.LENGTH_SHORT).show();
                    if (wifiP2pInfo.isGroupOwner) {
                        isClient = false;
                        server = new Server(handler, getApplicationContext());
                        new Thread(server).start();
                        ServerHolder.setServer(server);
                        btnRoom.setText(R.string.start_game);
                        btnRoom.setEnabled(true);
                    } else {
                        client = new Client(groupOwnerAddress, handler, getApplicationContext(), MainActivity.this);
                        new Thread(client).start();
                        ClientHolder.setClient(client);
                    }
                    btnSend.setEnabled(true);
                }
            }
        }
    };

    public void hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(() -> ActivityCompat.requestPermissions(this, permissions, 1));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
            recreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    public void launchGameRoomActivity() {
        startActivity(new Intent(this, GameRoomActivity.class));
    }
}