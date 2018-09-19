# jiAgora
Small companion REST server to keep track of active rooms and users for a jicofo instance.

# Usage
Insert POST querries to `/userJoin` and `/userLeave` in jicofo and `/` will contains the list of rooms and the count of users per room.
