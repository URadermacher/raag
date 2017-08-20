select count(*) count, 
	r.name, 
	i.name
from performance p
join raga r on r.id = p.ragaid
join performanceartist pa on pa.artistid = p.artistid and pa.storeid = p.storeid
join instrument i on i.id = pa.instrumentid
where i.name = 'Sitar'
group by p.ragaid
order by count desc