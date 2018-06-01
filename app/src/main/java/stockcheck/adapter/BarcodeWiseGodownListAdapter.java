package stockcheck.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerStockCheck;

public class BarcodeWiseGodownListAdapter extends RecyclerView.Adapter<BarcodeWiseGodownListAdapter.RecyclerHolder>{

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String,String>> mapList;
	DatabaseSqlLiteHandlerStockCheck DBHandler;
	private String Barcode;
	public BarcodeWiseGodownListAdapter(Context context, List<Map<String,String>> mapList, String Barcode) {
		this.context=context;
		this.mapList=mapList;
		this.Barcode=Barcode;
		this.inflater = LayoutInflater.from(context);
		this.DBHandler = new DatabaseSqlLiteHandlerStockCheck(context);
	}
	@Override
	public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.cardview_stockcheck_barcodewise_godown_design, parent, false);
		RecyclerHolder viewHolder=new RecyclerHolder(v);
		return viewHolder;
	}
	@Override
	public void onBindViewHolder(RecyclerHolder holder, final int position) {
		int pos=position;
		holder.txtGodownName.setText(""+mapList.get(position).get("GodownName"));
		List<Map<String,String>> mapListColor=DBHandler.getBarcodeWiseColor(Barcode);
		holder.txtColor.setText(""+mapListColor.get(0).get("ColorName"));
		List<Map<String,String>> mapListStock=DBHandler.getBarcodeWiseStock(mapList.get(position).get("GodownID"),mapListColor.get(0).get("ColorID"));
		holder.txtStock.setText(""+mapListStock.get(0).get("Stock"));
	}
	@Override
	public int getItemCount() {

		return mapList==null?0:mapList.size();
	}
	public class RecyclerHolder extends RecyclerView.ViewHolder {

		CardView cardView;
		TextView txtGodownName,txtStock,txtColor;

		public RecyclerHolder(View itemView) {
			super(itemView);

			cardView = (CardView) itemView.findViewById(R.id.card_view);
			txtColor = (TextView) itemView.findViewById(R.id.txtView_ColorName);
			txtStock = (TextView) itemView.findViewById(R.id.txtView_Barcodewise_Stock);
			txtGodownName = (TextView) itemView.findViewById(R.id.textView_GodownName);
		}
	}
}
