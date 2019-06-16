select 
	p.title, 
	r.name, 
	a.firstname, a.lastname,
	i.name,
	pp.duration, pp.filename
from performance p
join raga r on r.id = p.ragaid
join artist a on a.id = p.artistid
join performanceartist pa on pa.artistid = p.artistid and pa.storeid = p.storeid
join instrument i on i.id = pa.instrumentid
join performancepart pp on pp.storeid = p.storeid
where r.name = 'Hemant'
order by a.lastname	