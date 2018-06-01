package ip2sl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.singlagroup.R;
import com.singlagroup.customwidgets.CClient;
import com.singlagroup.customwidgets.EditSpinner;

import org.json.JSONException;
import org.json.JSONObject;

public class IP2SLActivity extends AppCompatActivity implements TCPListener{
  private static String TAG = IP2SLActivity.class.getSimpleName();
  private CClient mClient;
  private TcpClient mTcpClient;

  private TextInputLayout txtEditHexaCode;
  private TCPCommunicator tcpClient;
  private ProgressDialog dialog;
  public static String currentUserName;
  private Handler UIHandler = new Handler();
  private boolean isFirstLoad=true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ip2sl);

    final TextInputLayout txtEditIpAddress = (TextInputLayout) findViewById(R.id.editText_ip_address);
    final TextInputLayout txtEditPort = (TextInputLayout) findViewById(R.id.editText_port);
    txtEditHexaCode = (TextInputLayout) findViewById(R.id.editText_HexaCode);
    final EditSpinner editSpinner = (EditSpinner) findViewById(R.id.editSpinner);
    txtEditIpAddress.getEditText().setText("192.168.8.24");
    txtEditPort.getEditText().setText("4999");
    txtEditHexaCode.getEditText().setText("#0101430200FF05");
    AutoText();
    //EditSpinnerCall(editSpinner);
    //EditSpinner1(editSpinner);

    Button btnConnect = (Button) findViewById(R.id.button_connect);
    Button btnSubmit = (Button) findViewById(R.id.button_submit);

    btnConnect.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
          new ConnectTask().execute("testing");
        //ConnectToServer();
      }
    });
    btnSubmit.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        String ip = txtEditIpAddress.getEditText().getText().toString().trim();
        String port = txtEditPort.getEditText().getText().toString().trim();
        String hexa = txtEditHexaCode.getEditText().getText().toString().trim();
        if (mTcpClient != null) {
          mTcpClient.sendMessage("testing");
        }
//        if (!ip.isEmpty() && !port.isEmpty() && !hexa.isEmpty()) {
//          CallClient(ip, Integer.valueOf(port), hexa);
//          Toast.makeText(IP2SLActivity.this, "Hexa Code:" + hexa, Toast.LENGTH_SHORT).show();
//        } else {
//          Toast.makeText(IP2SLActivity.this, "Ip, Port and Hexa Code are cann't be blank", Toast.LENGTH_SHORT).show();
//        }

//        if (mTcpClient != null) {
//          mTcpClient.stopClient();
//        }
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  public void CallClient(String ip, int port, String HexaCode) {

    mClient = new CClient(ip, port);
    Thread myThready = new Thread(mClient);
    myThready.start();

    mClient.Send(HexaCode);
  }

  private void AutoText(){
    //Creating the instance of ArrayAdapter containing list of fruit names
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, getResources().getStringArray(R.array.ItemCategory));
    //Getting the instance of AutoCompleteTextView
    AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
    actv.setThreshold(1);//will start working from first character
    actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    actv.setTextColor(Color.RED);
  }
  private void EditSpinner1(EditSpinner mEditSpinner1){
    ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
            getResources().getStringArray(R.array.ItemCategory));
    mEditSpinner1.setAdapter(adapter);

    // triggered when dropdown popup window dismissed
    mEditSpinner1.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        Log.d("TAG", "mEditSpinner1 popup window dismissed!");
      }
    });

    // triggered when dropdown popup window shown
    mEditSpinner1.setOnShowListener(new EditSpinner.OnShowListener() {
      @Override
      public void onShow() {
        // maybe you want to hide the soft input panel when the popup window is showing.
        hideSoftInputPanel();
      }
    });
  }
  private void EditSpinnerCall(final EditSpinner mEditSpinner2) {
    final String[] stringArray2 = getResources().getStringArray(R.array.ItemCategory);
    mEditSpinner2.setDropDownDrawable(getResources().getDrawable(R.drawable.spinner), 25, 25);
    mEditSpinner2.setDropDownDrawableSpacing(50);
    mEditSpinner2.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringArray2));

    // it converts the item in the list to a string shown in EditText.
    mEditSpinner2.setItemConverter(new EditSpinner.ItemConverter() {
      @Override
      public String convertItemToString(Object selectedItem) {
        if (selectedItem.toString().equals(stringArray2[stringArray2.length - 1])) {
          return "";
        } else {
          return selectedItem.toString();
        }
      }
    });

    // triggered when one item in the list is clicked
    mEditSpinner2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("TAG", "onItemClick() position = " + position);
        if (position == stringArray2.length - 1) {
          showSoftInputPanel(mEditSpinner2);
        }
      }
    });

    mEditSpinner2.setOnShowListener(new EditSpinner.OnShowListener() {
      @Override
      public void onShow() {
        hideSoftInputPanel();
      }
    });

    // select the first item initially
    mEditSpinner2.selectItem(0);
  }


  private void hideSoftInputPanel() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      //imm.hideSoftInputFromWindow(mEditSpinner1.getWindowToken(), 0);
    }
  }

  private void showSoftInputPanel(View view) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.showSoftInput(view, 0);
    }
  }


  public class ConnectTask extends AsyncTask<String, String, TcpClient> {

    @Override
    protected TcpClient doInBackground(String... message) {

      //we create a TCPClient object
      mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
        @Override
        //here the messageReceived method is implemented
        public void messageReceived(String message) {
          //this method calls the onProgressUpdate
          publishProgress(message);
        }
      });
      mTcpClient.run();

      return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
      super.onProgressUpdate(values);
      //response received from server
      Log.d("test", "response " + values[0]);
      //process server response here....
      Toast.makeText(getApplicationContext(), "Response: " + values[0], Toast.LENGTH_LONG).show();

    }
  }


  private void ConnectToServer() {
    setupDialog();
    tcpClient = TCPCommunicator.getInstance();
    TCPCommunicator.addListener(this);
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    tcpClient.init(settings.getString(EnumsAndStatics.SERVER_IP_PREF, "192.168.8.25"),
            Integer.parseInt(settings.getString(EnumsAndStatics.SERVER_PORT_PREF, "4999")));
  }



  private void setupDialog() {
    dialog = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
    //dialog.setTitle("Loading");
    dialog.setMessage("Please wait...");
    dialog.setIndeterminate(true);
    dialog.show();
  }

  @Override
  protected void onStop()
  {
    super.onStop();

  }
  @Override
  protected void onResume()
  {
    super.onResume();
    //setContentView(R.layout.main_screen);
//    if(!isFirstLoad)
//    {
//      TCPCommunicator.closeStreams();
//      ConnectToServer();
//    }
//    else
//      isFirstLoad=false;
  }
  public void btnSendClick(View view)
  {
    JSONObject obj = new JSONObject();
    //EditText txtName= (EditText)findViewById(R.id.txtUserName);
    if(txtEditHexaCode.getEditText().getText().toString().length()==0)
    {
      Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
      return;
    }
    try
    {
      obj.put(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON, EnumsAndStatics.MessageTypes.MessageFromClient);
      obj.put(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON, txtEditHexaCode.getEditText().getText().toString());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    TCPCommunicator.writeToSocket(obj,UIHandler,this);
    //dialog.show();

  }

  @Override
  public void onTCPMessageRecieved(String message) {
    // TODO Auto-generated method stub
    final String theMessage=message;
    try {
      JSONObject obj = new JSONObject(message);
      String messageTypeString=obj.getString(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON);
      EnumsAndStatics.MessageTypes messageType = EnumsAndStatics.getMessageTypeByString(messageTypeString);

      switch(messageType)
      {

        case MessageFromServer:
        {

          runOnUiThread(new Runnable() {

            @Override
            public void run() {
              // TODO Auto-generated method stub
              //EditText editTextFromServer =(EditText)findViewById(R.id.editTextFromServer);
              txtEditHexaCode.getEditText().setText(theMessage);
            }
          });

          break;
        }


      }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Override
  public void onTCPConnectionStatusChanged(boolean isConnectedNow) {
    // TODO Auto-generated method stub
    if(isConnectedNow)
    {
      runOnUiThread(new Runnable() {

        @Override
        public void run() {
          // TODO Auto-generated method stub
          dialog.hide();
          Toast.makeText(getApplicationContext(), "Connected to server", Toast.LENGTH_SHORT).show();
        }
      });

    }
  }
  public void btnSettingsClicked(View view)
  {
//    Intent intent = new Intent(this,SettingsActivity.class);
//    startActivity(intent);
  }
}