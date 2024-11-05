## Quy tắc làm việc trên Github
- Chỉ commit khi đã hoàn thành một tính năng nào đó, không commit khi code còn lỗi
- Chỉ commit code trên nhánh cá nhân, **không** trực tiếp commit lên master (đã cài protection rule)
- **Trước khi code trên nhánh cá nhân**: Pull code từ master về (chú ý thư mục Java và file main_activity.xml vì mọi người cùng thao tác trên đây dễ gây conflict
- **Khi muốn code được merge vào master**: Tạo pull request -> Nhờ ít nhất 1 bạn trong team review
- **Sau khi code đã được merge vào master**: Thông báo với team để nhắc mọi người pull code từ master về trước khi code tiếp
- Hạn chế chỉnh sửa file code không do mình đảm nhận để tránh conflict (trừ khi có task cần phải chỉnh sửa trên file code của bạn khác)
---
## UI implementation:
- Mỗi người muốn làm giao diện nào thì tạo 1 Fragment cho giao diện đó (Home/Calendar/List/Settings)
- Có thể tham khảo CalendarFragment.java / fragment_calendar.xml 
