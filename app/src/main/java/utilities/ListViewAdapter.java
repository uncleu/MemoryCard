package utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.memorycard.android.memorycardapp.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListViewAdapter extends BaseAdapter{

    public Context context;
    private List<Map<String, Objects>> listItems;
    private LayoutInflater listContainer;
    public final class ListItemView{
        public TextView tableName;
        public ProgressCircle prog;
    }

    public ListViewAdapter(Context context, List<Map<String, Objects>> listItems){
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;
    }


    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        if(convertView == null){
            listItemView = new ListItemView();
            convertView = listContainer.inflate(R.layout.cards_group_list_item, null);
            listItemView.tableName = (TextView)convertView.findViewById(R.id.text_view);
            listItemView.prog = (ProgressCircle)convertView.findViewById(R.id.circleProgressbar);
        }else{
            listItemView = (ListItemView)convertView.getTag();
        }

        listItemView.tableName.setText((listItems.get(position).get("table")).toString());
        listItemView.prog.setProgress(Integer.parseInt((listItems.get(position).get("pro")).toString()));
        return convertView;
    }


}
