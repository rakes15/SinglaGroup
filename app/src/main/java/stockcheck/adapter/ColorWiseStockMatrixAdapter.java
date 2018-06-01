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

import java.util.List;
import java.util.Map;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerStockCheck;

public class ColorWiseStockMatrixAdapter extends RecyclerView.Adapter<ColorWiseStockMatrixAdapter.RecyclerHolder>{

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String,String>> mapList;
	DatabaseSqlLiteHandlerStockCheck DBHandler;
	private String Barcode;
	public ColorWiseStockMatrixAdapter(Context context, List<Map<String,String>> mapList,String Barcode) {
		this.context=context;
		this.mapList=mapList;
		this.Barcode=Barcode;
		this.inflater = LayoutInflater.from(context);
		this.DBHandler = new DatabaseSqlLiteHandlerStockCheck(context);
	}
	@Override
	public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.cardview_order_details_design, parent, false);
		RecyclerHolder viewHolder=new RecyclerHolder(v);
		return viewHolder;
	}
	@Override
	public void onBindViewHolder(RecyclerHolder holder, final int position) {
		int pos=position;
		holder.imageViewDelete.setVisibility(View.GONE);
		holder.txtTitle.setText(""+mapList.get(position).get("GodownName"));
		holder.tableLayout.removeAllViews();
		holder.tableLayout.removeAllViewsInLayout();
		//TODO: Grid of Size by Stock and bookQty
		List<Map<String, String>> sizeList = DBHandler.getBarcodeScannerMatrixsize(mapList.get(position).get("ItemID"));
		List<Map<String, String>> colorList = DBHandler.getBarcodeWiseColor(Barcode);
		List<Map<String, String>> mdQtyhList = null;
		int[][] rowTotArr = new int[sizeList.size() + 1][1];
		int[] columnTotaltArr = new int[1];
		int Qty = 0;
		int i = 0;

		TableRow tableRow1 = new TableRow(context);
		tableRow1.setId(i + 100);
		tableRow1.setBackgroundColor(Color.LTGRAY);

		TextView txt = new TextView(context);
		txt.setId(i + 11);
		txt.setText("Color");
		txt.setTextColor(Color.BLACK);
		txt.setPadding(5, 5, 5, 5);
		tableRow1.addView(txt);// add the column to the table row here

		for (int k = 0; k < sizeList.size(); k++) {
			TextView tv = new TextView(context);
			tv.setId(k);// define id that must be unique
			tv.setTag(sizeList.get(k).get("SizeID"));
			tv.setText(sizeList.get(k).get("SizeName")); // set the text for the header
			tv.setTextColor(Color.BLUE); // set the color
			tv.setPadding(10, 5, 10, 5); // set the padding (if required)
			tableRow1.addView(tv); // add the column to the table row here
		}
		TextView tv = new TextView(context);
		tv.setId(i + 15);// define id that must be unique
		tv.setText("T Qty"); // set the text for the header
		tv.setTextColor(Color.BLACK); // set the color
		tv.setPadding(10, 5, 10, 5); // set the padding (if required)
		tableRow1.addView(tv); // add the column to the table row here
		holder.tableLayout.addView(tableRow1);
		int j = 0, dynamicID = 0;
		for (int m = 0; m < colorList.size(); m++) {
			//TODO; Row 2
			TableRow tableRow2 = new TableRow(context);
			tableRow2.setId(m + 20);
			tableRow2.setBackgroundColor(Color.WHITE);

			columnTotaltArr[0] = 0;
			try {
				TextView tvColor = new TextView(context);
				tvColor.setId(89 + i);// define id that must be unique
				tvColor.setText(colorList.get(m).get("ColorName")); // set the text for the header
				tvColor.setTextColor(Color.BLACK); // set the color
				tvColor.setPadding(5, 5, 5, 5); // set the padding (if required)
				tableRow2.addView(tvColor); // add the column to the table row here
			} catch (Exception e) {
				Log.e("Exception", "Color:" + e.getMessage());
			}
			// inner for loop
			for (j = 0; j < sizeList.size(); j++) {
				try {
					mdQtyhList = DBHandler.getStockGodownWise(colorList.get(m).get("ColorID"), sizeList.get(j).get("SizeID"),mapList.get(pos).get("ItemID"), mapList.get(pos).get("GodownID"));
					if (mdQtyhList.isEmpty()) {
						Qty = 0;
					} else {
						Qty = Integer.valueOf(mdQtyhList.get(0).get("Qty"));
						columnTotaltArr[0] += Qty;//Add value
					}
					tv = new TextView(context);
					tv.setId(dynamicID);// define id that must be unique
					tv.setText(String.valueOf(Qty) + ""); // set the text for the header
					tv.setTextColor(Color.BLACK); // set the color
					tv.setPadding(10, 5, 10, 5); // set the padding (if required)
					tableRow2.addView(tv); // add the column to the table row here

					rowTotArr[j][0] += Qty;
					dynamicID++;
				} catch (Exception e) {
					Log.e("Exception", "Qty:" + e.getMessage());
				}
			}
			rowTotArr[j][0] += columnTotaltArr[0];

			tv = new TextView(context);
			tv.setId(i + 20);// define id that must be unique
			tv.setText(columnTotaltArr[0] + ""); // set the text for the header
			tv.setTextColor(Color.BLUE); // set the color
			tv.setPadding(10, 5, 10, 5); // set the padding (if required)
			tableRow2.addView(tv); // add the column to the table row here

			holder.tableLayout.addView(tableRow2);
		}
		//TODO: Row3
		TableRow tableRow3 = new TableRow(context);
		tableRow3.setId(i + 200);
		tableRow3.setBackgroundColor(Color.DKGRAY);

		tv = new TextView(context);
		tv.setId(i + 30);// define id that must be unique
		tv.setText("G Total"); // set the text for the header
		tv.setTextColor(Color.RED); // set the color
		tv.setPadding(10, 5, 10, 5); // set the padding (if required)
		tableRow3.addView(tv); // add the column to the table row here
		for (int f = 0; f < sizeList.size() + 1; f++) {
			tv = new TextView(context);
			tv.setId(f + 40);// define id that must be unique
			tv.setText(rowTotArr[f][0] + ""); // set the text for the header
			tv.setTextColor(Color.WHITE); // set the color
			tv.setPadding(10, 5, 10, 5); // set the padding (if required)
			tableRow3.addView(tv); // add the column to the table row here
		}
		holder.tableLayout.addView(tableRow3);
	}
	@Override
	public int getItemCount() {

		return mapList==null?0:mapList.size();
	}
	public class RecyclerHolder extends RecyclerView.ViewHolder {

		CardView cardView;
		TableLayout tableLayout;
		ImageView imageViewDelete;
		TextView txtTitle;

		public RecyclerHolder(View itemView) {
			super(itemView);

			cardView = (CardView) itemView.findViewById(R.id.card_view);
			tableLayout = (TableLayout) itemView.findViewById(R.id.tableLayout1);
			imageViewDelete = (ImageView) itemView.findViewById(R.id.imageView_delete);
			txtTitle = (TextView) itemView.findViewById(R.id.textView_Title);
		}
	}
}
