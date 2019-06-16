select r.name
from raga r
where r.id not in 
	(select distinct(p.ragaid)
	 from Performance p)