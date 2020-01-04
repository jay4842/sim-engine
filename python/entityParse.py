# TODO parse entity logs
# TODO store entity info to a log at runtime
import mysqlConnection as sql

def parse_entity_report(lines, file_name):
    if(len(lines) < 5):
        print('Not enough lines for this report -> {}'.format(file_name))
        return
    
    steps = []
    sim_id = file_name.split('/')[-1].split('_')[-1].split('.')[0]
    print('Linked to sim # {}'.format(sim_id))
    for idx, line in enumerate(lines):
        print('step: {}'.format(idx))
        line = line[2:len(line)-2].split(',')
        for sub in line:
            sub = sub[1:len(sub)-1].split('_')
            make_single_entity_query(sim_id, idx, sub)
        print()
        exit(1)

# takes info for one entity and create a query for it
def make_single_entity_query(sim_id, step, entity_info):
    # entity_info = [id, y, x, pos]
    entity_id = entity_info[0]
    gps = '_'.join(entity_info[1:])
    query = 'INSERT INTO entity_info_at_step(step_id, sim_id, entity_id, gps)'\
        ' VALUE({0}, {1}, {2}, \'{3}\');'.format(step, )
    



    