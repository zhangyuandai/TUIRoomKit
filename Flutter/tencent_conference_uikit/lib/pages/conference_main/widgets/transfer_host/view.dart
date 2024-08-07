import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:tencent_conference_uikit/common/index.dart';

import 'index.dart';
import 'widgets/widgets.dart';

class TransferHostWidget extends GetView<TransferHostController> {
  const TransferHostWidget({Key? key}) : super(key: key);

  Widget _buildView(Orientation orientation) {
    return BottomSheetWidget(
      height: orientation == Orientation.portrait
          ? 758.0.scale375Height()
          : Get.height,
      isNeedDropDownButton: false,
      orientation: orientation,
      child: Column(
        children: [
          const TitleWidget(),
          SearchBarWidget(
            searchAction: (value) => controller.searchAction(value),
          ),
          SizedBox(height: 10.0.scale375Height()),
          const UserTableWidget(),
          SizedBox(height: 10.0.scale375Height()),
          SizedBox(
            height: 49.0.scale375Height(),
            width: orientation == Orientation.portrait
                ? 340.0.scale375()
                : 240.0.scale375(),
            child: ElevatedButton(
              style: RoomTheme.defaultTheme.menuButtonTheme.style,
              onPressed: () => controller.appointAndLeaveAction(),
              child: Text(
                'appointAndLeave'.roomTr,
                style: RoomTheme.defaultTheme.textTheme.bodyLarge,
              ),
            ),
          ),
          SizedBox(height: 10.0.scale375Height()),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TransferHostController>(
      init: TransferHostController(),
      id: "transfer_host",
      builder: (_) {
        return LayoutBuilder(
          builder: (BuildContext context, BoxConstraints constraints) {
            Orientation orientation = MediaQuery.of(context).orientation;
            return _buildView(orientation);
          },
        );
      },
    );
  }
}
