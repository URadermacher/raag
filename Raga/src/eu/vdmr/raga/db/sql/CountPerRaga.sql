select count(p.ragaid) CNT, r.name
from Performance p
join raga r on r.id = p.ragaid
group by p.ragaid
order by CNT