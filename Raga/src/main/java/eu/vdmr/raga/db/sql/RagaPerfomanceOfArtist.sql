select
r.name ,
pp.storeid, pp.filename,
p.title,
a.firstname, a.lastname
from PerformancePart pp
join Performance p on p.storeid = pp.storeid
join artist a on a.id = p.artistid
join raga r on r.id = p.ragaid
where r.name = 'Yaman'
and a.lastname = 'Chatterjee';