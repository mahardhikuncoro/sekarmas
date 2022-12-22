package ops.screen.fragment;

import base.data.laporan.DataLaporan;
import base.sqlite.model.TaskListDetailModel;

public interface TaskListInterface {

    void onListSelected(TaskListDetailModel list);
    void onOptionClick(DataLaporan list);
//    void detailSection(Integer position);
}
