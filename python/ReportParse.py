import argparse
import sys
#import os
import glob

import simParse
import entityParse

parser = argparse.ArgumentParser(description='Process a report')
#parser.add_argument('--input_file', dest='input_file', default='/logs/EntityLog/', help='input file path')
#parser.add_argument('--report_type', dest='report_type', default='entity_report', help='input file type')
parser.add_argument('--input_file', dest='input_file', default='/logs/simReports/sim_report.txt', help='input file path')
parser.add_argument('--report_type', dest='report_type', default='sim_report', help='input file type')
args = parser.parse_args()

def parse_file(input_path, type_of_report, database='sim_test'):
    """ a sim_report is a detailed report of population information for each turn """
    print("parsing: " + input_path)
    print("Type   : " + type_of_report)
    lines = []
    if(type_of_report is 'sim_report' and '.txt' in input_path):
        print('creating sim report parsed file')
        with open(input_path, 'r') as file:
            for line in file:
                lines.append(line)
            #
            file.close()
        
        simParse.parse_sim_report(lines, database)
    elif(type_of_report is 'entity_report' and input_path[-1] is '/'):
        files = glob.glob('{}*.txt'.format(input_path))
        files.sort()
        print('found {} files'.format(len(files)))
        for filename in files:
            lines = []
            with open(filename, 'r') as file:
                for line in file:
                    lines.append(line)
                #
                file.close()
            entityParse.parse_entity_report(lines, filename)

# MAIN
if __name__ == "__main__":
    path = sys.path[0]
    path = path.split('/')
    path = "/".join(path[0:len(path)-1])
    #parse_file(path + args.input_file, args.report_type)

    parse_file(path + '/logs/simReports/sim_report.txt', 'sim_report')
    parse_file(path + '/logs/EntityLog/', 'entity_report')
    
    #string = '{3340'
    #print(string.split('{')[1])