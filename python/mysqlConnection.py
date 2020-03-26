import pymysql
import paramiko
from sshtunnel import SSHTunnelForwarder
from os.path import expanduser
from progress.bar import ChargingBar

# https://stackoverflow.com/questions/21903411/enable-python-to-connect-to-mysql-via-ssh-tunnelling
# https://stackoverflow.com/questions/55617520/unable-to-make-tls-tcp-connection-to-remote-mysql-server-with-pymysql-other-too

def execute_query(query):
    print('exe: {}'.format(query))
    home = expanduser('~')
    mypkey = paramiko.RSAKey.from_private_key_file(home + '/Desktop/ssh/drop_key', password='password')
    # if you want to use ssh password use - ssh_password='your ssh password', bellow

    sql_hostname = '0.0.0.0'
    sql_username = 'dev'
    sql_password = 'password'
    sql_main_database = 'sim_test'
    sql_port = 3306
    ssh_host = '0.0.0.0'
    ssh_user = 'dev'
    ssh_port = 22
    #sql_ip = '######'
    #context = ssl.create_default_context()
    data = None
    with SSHTunnelForwarder(
        (ssh_host, ssh_port),
        ssh_username=ssh_user,
        ssh_pkey=mypkey,
        remote_bind_address=(sql_hostname, sql_port)) as tunnel:
            conn = pymysql.connect(host='127.0.0.1', user=sql_username,
                    passwd=sql_password, db=sql_main_database,
                    port=tunnel.local_bind_port,
                    ssl={"fake_flag_to_enable_tls":True})
            cursor = conn.cursor()
            try:
                data = cursor.execute(query)
            except Exception as ex:
                print(ex)
                print('query: {}'.format(query))
                exit(1)
            conn.close()
    #
    return data

def execute_many(query_list):
    print('Executing {} queries...'.format(len(query_list)))
    home = expanduser('~')
    mypkey = paramiko.RSAKey.from_private_key_file(home + '/Desktop/ssh/drop_key', password='jellyfish42')
    # if you want to use ssh password use - ssh_password='your ssh password', bellow

    sql_hostname = '0.0.0.0'
    sql_username = 'dev'
    sql_password = 'password'
    sql_main_database = 'sim_test'
    sql_port = 3306
    ssh_host = '0.0.0.0'
    ssh_user = 'dev'
    ssh_port = 22
    #sql_ip = '0.0.0.0'
    #context = ssl.create_default_context()
    #data = []
    fails = 0
    passes = 0
    with SSHTunnelForwarder(
        (ssh_host, ssh_port),
        ssh_username=ssh_user,
        ssh_pkey=mypkey,
        remote_bind_address=(sql_hostname, sql_port)) as tunnel:
            conn = pymysql.connect(host='127.0.0.1', user=sql_username,
                    passwd=sql_password, db=sql_main_database,
                    port=tunnel.local_bind_port,
                    ssl={"fake_flag_to_enable_tls":True})
            cursor = conn.cursor()
            suffix = '%(index)d/%(max)d [%(elapsed)d / %(eta)d / %(eta_td)s]'
            with ChargingBar('executing', max=len(query_list), suffix=suffix) as bar:
                for query in query_list:
                    try:
                        result = cursor.execute(query)
                        passes+=1
                        conn.commit()
                    except Exception as ex:
                        print(ex)
                        print('query: {}'.format(query))
                        exit(1)
                        fails+=1
                    bar.next()
            conn.close()
    print("passes: {0} | fails: {1}".format(passes, fails))