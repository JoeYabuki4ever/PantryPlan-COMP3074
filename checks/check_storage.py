"""Exercise the app's schema and read queries against an on-disk SQLite database."""
import re
import sqlite3
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
src = (root / "app/src/main/java/com/pantryplan/app/MainActivity.java").read_text()
with tempfile.TemporaryDirectory() as directory:
    path = Path(directory) / "pantry.db"
    db = sqlite3.connect(path)
    for ddl in re.findall(r'db.execSQL\("(CREATE TABLE[^"]+)"\)', src):
        db.execute(ddl)
    db.execute("insert into food(name,quantity,unit,location,date) values(?,?,?,?,?)",
               ("O'Brien oats","2","bags","Pantry",""))
    db.execute("update food set quantity='1' where id=1")
    db.execute("insert into shopping(name,quantity) select name, quantity || ' ' || unit from food")
    db.commit()
    db.close()
    db = sqlite3.connect(path)
    foods = re.search(r'"(SELECT id,name,quantity,unit,location,date FROM food ORDER BY [^"]+)"',src)[1]
    shopping = re.search(r'"(SELECT id,name,quantity,done FROM shopping ORDER BY [^"]+)"',src)[1]
    assert db.execute(foods).fetchone()[1:] == ("O'Brien oats","1","bags","Pantry","")
    db.execute("delete from food where id=1")
    assert db.execute(shopping).fetchone()[1:] == ("O'Brien oats","1 bags",0)
    db.execute("insert into shopping(name,quantity) values('Milk','1 bottle')")
    db.execute("update shopping set done=1 where id=1")
    db.execute("delete from shopping where done=1")
    assert [r[1] for r in db.execute(shopping)] == ["Milk"]
    db.close()
print("Passed SQLite reopen, edit, independent shopping copy, and clear-purchased checks.")
