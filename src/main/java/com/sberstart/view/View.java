package com.sberstart.view;

import com.sberstart.entities.Tower;

public interface View {
    void refreshView();
    void refreshHeader();
    void refreshTowerInfo(Tower tower);
}
