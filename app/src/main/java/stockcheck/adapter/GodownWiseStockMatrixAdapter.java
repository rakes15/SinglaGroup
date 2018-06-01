package stockcheck.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.GlobleValues;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ConditionLibrary;
import com.singlagroup.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import report.forceclose.model.ItemDetails;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerStockCheck;
import stockcheck.InTransitOrJobberWarehouseReport;
import stockcheck.model.Godown;

public class GodownWiseStockMatrixAdapter extends RecyclerView.Adapter<GodownWiseStockMatrixAdapter.RecyclerHolder>{

	private Context context;
	private LayoutInflater inflater;
	private List<Godown> mapList;
	DatabaseSqlLiteHandlerStockCheck DBHandler;
	private String Barcode;
	private int MDApplicable=0, SubItemApplicable=0,flag=0;
	public GodownWiseStockMatrixAdapter(Context context, List<Godown> mapList, int MDApplicable, int SubItemApplicable, String Barcode, int flag) {
		this.context=context;
		this.mapList=mapList;
		this.MDApplicable=MDApplicable;
		this.SubItemApplicable=SubItemApplicable;
		this.Barcode=Barcode;
		this.flag=flag;
		this.inflater = LayoutInflater.from(context);
		this.DBHandler = new DatabaseSqlLiteHandlerStockCheck(context);
	}
	@Override
	public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = inflater.inflate(R.layout.cardview_table_layout_horizontal_scroll, parent, false);
		RecyclerHolder viewHolder=new RecyclerHolder(v);
		return viewHolder;
	}
	@Override
	public void onBindViewHolder(RecyclerHolder holder, final int position) {
		int pos=position;
		CLickEvents(mapList.get(position), holder.tableLayout, holder.tableLayout2);
		DisplayGodownName(holder.tableLayout,mapList.get(position).getGodownName());
		if(MDApplicable == 1) {
			StockMatrixMDDetails(holder.tableLayout2,mapList.get(position).getGodownID(),mapList.get(position).getItemID());
		}else {
			if(SubItemApplicable == 1) {
				SubItemDetails(holder.tableLayout2,mapList.get(position).getGodownID(),mapList.get(position).getItemID());
			} else {
				ItemOnlyDetails(holder.tableLayout2,mapList.get(position).getGodownID(),mapList.get(position).getItemID());
			}
		}
	}
	@Override
	public int getItemCount() {

		return mapList==null?0:mapList.size();
	}
	public class RecyclerHolder extends RecyclerView.ViewHolder {

		CardView cardView;
		TableLayout tableLayout,tableLayout2;
		TextView txtTitle;
		public RecyclerHolder(View itemView) {
			super(itemView);
			cardView = (CardView) itemView.findViewById(R.id.card_view);
			txtTitle = (TextView) itemView.findViewById(R.id.textView_Header);
			tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
			tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
		}
	}
	//TODO: Click Events
	private void CLickEvents(Godown godown, TableLayout tableLayout, TableLayout tableLayout2){
		//TODO: set Tag
		tableLayout.setTag(godown);
		tableLayout2.setTag(godown);
		//TODO: On Click
		tableLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Godown godown = (Godown)view.getTag();
				if (godown.getGodownID().equals("00000000-0000-0000-0000-000000000000")) {
					//TODO: In Transit
					Intent intent = new Intent(context, InTransitOrJobberWarehouseReport.class);
					intent.putExtra("Godown", godown);
					intent.putExtra("MDApplicable", MDApplicable);
					intent.putExtra("SubItemApplicable", SubItemApplicable);
					intent.putExtra("Type", "1");
					context.startActivity(intent);
				}else if(godown.getGodownID().equals("0F8EFE31-4319-4CC8-A7F8-E203226BEB07")){
					//TODO: In Jobber
					Intent intent = new Intent(context, InTransitOrJobberWarehouseReport.class);
					intent.putExtra("Godown", godown);
					intent.putExtra("MDApplicable", MDApplicable);
					intent.putExtra("SubItemApplicable", SubItemApplicable);
					intent.putExtra("Type", "0");
					context.startActivity(intent);
				}
			}
		});
		tableLayout2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Godown godown = (Godown)view.getTag();
				if (godown.getGodownID().equals("00000000-0000-0000-0000-000000000000")) {
					//TODO: In Transit
					Intent intent = new Intent(context, InTransitOrJobberWarehouseReport.class);
					intent.putExtra("Godown", godown);
					intent.putExtra("MDApplicable", MDApplicable);
					intent.putExtra("SubItemApplicable", SubItemApplicable);
					intent.putExtra("Type", "1");
					context.startActivity(intent);
				}else if(godown.getGodownID().equals("0F8EFE31-4319-4CC8-A7F8-E203226BEB07")){
					//TODO: In Jobber
					Intent intent = new Intent(context, InTransitOrJobberWarehouseReport.class);
					intent.putExtra("Godown", godown);
					intent.putExtra("MDApplicable", MDApplicable);
					intent.putExtra("SubItemApplicable", SubItemApplicable);
					intent.putExtra("Type", "0");
					context.startActivity(intent);
				}
			}
		});
	}
	//TODO: Display Godown Name
	private void DisplayGodownName(TableLayout tableLayout,String GodownName){
		//TODO: TableLayout set
		tableLayout.removeAllViewsInLayout();
		tableLayout.removeAllViews();
		//TODO: Godown Name
		View v = inflater.inflate(R.layout.table_row_single_column, tableLayout, false);
		TextView txtHeader= (TextView) v.findViewById(R.id.content);
		txtHeader.setText(""+GodownName);
		txtHeader.setPadding(10,10,10,10);
		txtHeader.setGravity(Gravity.CENTER_HORIZONTAL);
		txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
		txtHeader.setTypeface(null,Typeface.BOLD);
		tableLayout.addView(v);
	}
	//TODO: Display Stock Grid of Multi Details
	private void StockMatrixMDDetails(TableLayout tableLayout,String GodownID,String ItemID){
		tableLayout.removeAllViews();
		tableLayout.removeAllViewsInLayout();
		//TODO: Grid of Size by Stock and bookQty
		List<Map<String,String>> sizeList = (flag == 0 ? DBHandler.getBarcodeScannerMatrixsizeGodown("",GodownID) : DBHandler.getBarcodeWiseSizeList(ItemID));
		List<Map<String,String>> colorList = (flag == 0 ? DBHandler.getBarcodeScannerMatrixColorGodown(ItemID,GodownID) : DBHandler.getBarcodeWiseColorList(Barcode));
		List<Map<String, String>> mdQtyList = new ArrayList<>();
		int[][] rowTotArr=new  int[sizeList.size()+1][1];
		int[] columnTotaltArr=new  int[1];
		int Stock=0;

		int i=0;

		TableRow tableRow1=new TableRow(context);
		tableRow1.setId(i+100);
		tableRow1.setBackgroundColor(Color.LTGRAY);

		TextView txt = new TextView(context);
		txt.setId(i+11);
		txt.setText("Color");
		txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
		txt.setGravity(Gravity.LEFT);
		txt.setTextColor(Color.BLACK);
		txt.setPadding(5, 5, 5, 5);
		tableRow1.addView(txt);// add the column to the table row here

		for(int k=0;k<sizeList.size();k++)
		{
			TextView tv = new TextView(context);
			tv.setId(k);// define id that must be unique
			tv.setTag(sizeList.get(k).get("SizeID"));
			tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setText(sizeList.get(k).get("SizeName")); // set the text for the header
			tv.setTextColor(Color.BLUE); // set the color
			tv.setPadding(10, 5, 10, 5); // set the padding (if required)
			tableRow1.addView(tv); // add the column to the table row here
		}
		TextView tv = new TextView(context);
		tv.setId(i+15);// define id that must be unique
		tv.setText("Total"); // set the text for the header
		tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTextColor(Color.BLACK); // set the color
		tv.setPadding(10, 5, 10, 5); // set the padding (if required)
		tableRow1.addView(tv); // add the column to the table row here
		tableLayout.addView(tableRow1);
		int j = 0,dynamicID=0;
		for (int m = 0; m < colorList.size(); m++)
		{
			//TODO; Row 2
			TableRow tableRow2=new TableRow(context);
			tableRow2.setId(m+20);
			tableRow2.setBackgroundColor(Color.WHITE);

			columnTotaltArr[0]=0;
			try {
				TextView tvColor = new TextView(context);
				tvColor.setId(89+i);// define id that must be unique
				tvColor.setText(colorList.get(m).get("ColorName")); // set the text for the header
				tvColor.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
				tvColor.setGravity(Gravity.LEFT);
				tvColor.setTextColor(Color.BLACK); // set the color
				tvColor.setPadding(5, 5, 5, 5); // set the padding (if required)
				tableRow2.addView(tvColor); // add the column to the table row here
			} catch (Exception e) {
				Log.e("Exception", "Color:"+e.getMessage());
			}
			// inner for loop
			for (j = 0; j < sizeList.size(); j++)
			{
				try {
					mdQtyList=DBHandler.getStockGodownWise(colorList.get(m).get("ColorID"), sizeList.get(j).get("SizeID"),ItemID,GodownID);
					if(mdQtyList.isEmpty()){
						Stock=0;
					}else {
						Stock=Integer.valueOf(mdQtyList.get(0).get("Stock"));
						columnTotaltArr[0]+=Stock;//Add value
					}
					tv = new TextView(context);
					tv.setId(dynamicID);// define id that must be unique
					tv.setText(String.valueOf(Stock)); // set the text for the header
					tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
					tv.setGravity(Gravity.CENTER_HORIZONTAL);
					tv.setTextColor(Color.BLACK); // set the color
					tv.setPadding(10, 5, 10, 5); // set the padding (if required)
					tableRow2.addView(tv); // add the column to the table row here

					rowTotArr[j][0]+=Stock;
					dynamicID++;
				} catch (Exception e) {
					Log.e("Exception", "Qty:"+e.getMessage());
				}
			}
			rowTotArr[j][0]+=columnTotaltArr[0];

			tv = new TextView(context);
			tv.setId(i+20);// define id that must be unique
			tv.setText(String.valueOf(columnTotaltArr[0])); // set the text for the header
			tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setTextColor(Color.BLUE); // set the color
			tv.setPadding(10, 5, 10, 5); // set the padding (if required)
			tableRow2.addView(tv); // add the column to the table row here

			tableLayout.addView(tableRow2);
		}
		//TODO: Row3
		TableRow tableRow3=new TableRow(context);
		tableRow3.setId(i+200);
		tableRow3.setBackgroundColor(Color.LTGRAY);

		tv = new TextView(context);
		tv.setId(i+30);// define id that must be unique
		tv.setText("Grand Total"); // set the text for the header
		tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
		tv.setGravity(Gravity.LEFT);
		tv.setTextColor(Color.RED); // set the color
		tv.setPadding(10, 5, 10, 5); // set the padding (if required)
		tableRow3.addView(tv); // add the column to the table row here
		for(int f=0;f<sizeList.size()+1;f++)
		{
			tv = new TextView(context);
			tv.setId(f+40);// define id that must be unique
			tv.setText(String.valueOf(rowTotArr[f][0])); // set the text for the header
			tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setTextColor(Color.RED); // set the color
			tv.setPadding(10, 5, 10, 5); // set the padding (if required)
			tableRow3.addView(tv); // add the column to the table row here
		}
		tableLayout.addView(tableRow3);
	}
	//TODO: Display Stock Grid of SubItem Details
	private void SubItemDetails(TableLayout tableLayout,String GodownID,String ItemID){
		tableLayout.removeAllViews();
		tableLayout.removeAllViewsInLayout();
		List<Map<String,String>> dataListSubItem=DBHandler.getSubItemDetails(ItemID);
		if(!dataListSubItem.isEmpty()) {
			int i=0;
			TableRow tableRow=new TableRow(context);
			tableRow.setId(i+10);
			tableRow.setBackgroundColor(Color.DKGRAY);

			TextView txt = new TextView(context);
			txt.setId(i+1);
			txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			txt.setGravity(Gravity.LEFT);
			txt.setText("SubItem Code");
			txt.setTypeface(Typeface.DEFAULT_BOLD);
			txt.setTextColor(Color.WHITE);
			txt.setPadding(5, 5, 5, 5);
			tableRow.addView(txt);// add the column to the table row here

			txt = new TextView(context);
			txt.setId(i+2);
			txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			txt.setGravity(Gravity.CENTER_HORIZONTAL);
			txt.setText("SubItem Name");
			txt.setTypeface(Typeface.DEFAULT_BOLD);
			txt.setTextColor(Color.WHITE);
			txt.setPadding(5, 5, 5, 5);
			tableRow.addView(txt);// add the column to the table row here

			txt = new TextView(context);
			txt.setId(i+3);
			txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			txt.setGravity(Gravity.CENTER_HORIZONTAL);
			txt.setText("Stock");
			txt.setTypeface(Typeface.DEFAULT_BOLD);
			txt.setTextColor(Color.WHITE);
			txt.setPadding(5, 5, 5, 5);
			tableRow.addView(txt);// add the column to the table row here

			tableLayout.addView(tableRow);

			for (int j=0; j<dataListSubItem.size(); j++) {
				List<Map<String,String>> SubItemStock = DBHandler.getSubItemGodownStock(ItemID,dataListSubItem.get(j).get("SubItemID"), GodownID);
				String stock="0";
				if (SubItemStock.isEmpty()){
					stock = "0";
				}else {
					stock = ConditionLibrary.ConvertRoundOff2Decimal(SubItemStock.get(0).get("Stock"));
				}
				//TODO:Table Row2
				tableRow = new TableRow(context);
				tableRow.setId(j + 20);
				tableRow.setBackgroundColor(Color.TRANSPARENT);

				txt = new TextView(context);
				txt.setId(j + 1);
				txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
				txt.setGravity(Gravity.CENTER_HORIZONTAL);
				txt.setText("" + dataListSubItem.get(j).get("SubItemCode"));
				txt.setTypeface(Typeface.DEFAULT_BOLD);
				txt.setTextColor(Color.BLACK);
				txt.setPadding(5, 5, 5, 5);
				tableRow.addView(txt);// add the column to the table row here

				txt = new TextView(context);
				txt.setId(j + 2);
				txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
				txt.setGravity(Gravity.CENTER_HORIZONTAL);
				txt.setText("" + dataListSubItem.get(j).get("SubItemName"));
				txt.setTypeface(Typeface.DEFAULT_BOLD);
				txt.setTextColor(Color.BLUE);
				txt.setPadding(5, 5, 5, 5);
				tableRow.addView(txt);// add the column to the table row here

				txt = new TextView(context);
				txt.setId(j + 3);
				txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
				txt.setGravity(Gravity.CENTER_HORIZONTAL);
				txt.setText(""+stock);
				txt.setTypeface(Typeface.DEFAULT_BOLD);
				txt.setTextColor(Color.RED);
				txt.setPadding(5, 5, 5, 5);
				tableRow.addView(txt);// add the column to the table row here

				tableLayout.addView(tableRow);
			}
		}
	}
	//TODO: Display Stock Grid of Item Only Details
	private void ItemOnlyDetails(TableLayout tableLayout,String GodownID,String ItemID){
		tableLayout.removeAllViews();
		tableLayout.removeAllViewsInLayout();
		List<Map<String,String>> dataListItem=DBHandler.getWithoutMDItemDetails(ItemID);
		List<Map<String,String>> ItemOnlyStock=DBHandler.getItemOnlyGodown(ItemID,GodownID);
		String stock = "0";
		if (ItemOnlyStock.isEmpty()){
			stock = "0";
		}else {
			stock = ConditionLibrary.ConvertRoundOff2Decimal(ItemOnlyStock.get(0).get("Stock"));
		}
		if(!dataListItem.isEmpty())
		{
			int i=0;
			//TODO: 1st Row
			TableRow tableRow=new TableRow(context);
			tableRow.setId(i+10);
			tableRow.setBackgroundColor(Color.LTGRAY);

			TextView txt = new TextView(context);
			txt.setId(i+1);
			txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			txt.setGravity(Gravity.CENTER_HORIZONTAL);
			txt.setText("Stock");
			txt.setTypeface(Typeface.DEFAULT_BOLD);
			txt.setTextColor(Color.BLUE);
			txt.setPadding(5, 5, 5, 5);
			tableRow.addView(txt);// add the column to the table row here

			tableLayout.addView(tableRow);
			//TODO: 2nd Row
			tableRow=new TableRow(context);
			tableRow.setId(i+20);
			tableRow.setBackgroundColor(Color.TRANSPARENT);

			txt = new TextView(context);
			txt.setId(i+11);
			txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
			txt.setGravity(Gravity.CENTER_HORIZONTAL);
			txt.setText(stock+"");
			txt.setTypeface(Typeface.DEFAULT_BOLD);
			txt.setTextColor(Color.BLACK);
			txt.setPadding(5, 5, 5, 5);
			tableRow.addView(txt);// add the column to the table row here

			tableLayout.addView(tableRow);

		}
	}
}
