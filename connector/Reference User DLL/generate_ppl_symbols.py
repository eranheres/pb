import sys

template_filename = sys.argv[1]
input_filename = sys.argv[2]
output_filename = sys.argv[3]

v = [line[2:-3] for line in open(template_filename) if "##" in line]
with open(output_filename, "wt") as fout:
    with open(input_filename, "rt") as fin:
        for line in fin:
            fout.write(line.replace('<<SYMBOLS>>', '", "'.join(v)))
