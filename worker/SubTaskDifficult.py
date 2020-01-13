import csv
import json

class SubTaskDifficult:

    def __init__(self, e2wl, w2el, label_set):
        self.e2wl = e2wl
        self.w2el = w2el
        self.label_set = label_set

    def ComputeDifficult(self, e2wl, wm):
        result = {}
        for e, workerlabels in e2wl.items():
            last00 = 0.0
            last11 = 0.0
            lastpd0 = 0.0
            lastpd1 = 0.0
            e2l = []
            for idx in range(len(workerlabels)):
                e2lpd={}
                for label in self.label_set:
                    e2lpd[label]=1.0#l2pd[label]
                currentLabels = []
                # currentLabels.append(workerlabels[idx])
                # currentLabels.append(workerlabels[idx + 1])
                if idx==0:
                    currentLabels.append(workerlabels[idx])
                    #currentLabels.append(workerlabels[idx+1])
                else:
                    currentLabels.append(workerlabels[idx])
                    for label in self.label_set:
                        if label == '0':
                            e2lpd[label] = lastpd0
                        if label == '1':
                            e2lpd[label] = lastpd1
                for candlabel in self.label_set:
                    index = 0
                    for worker, label in currentLabels:
                        ## 必须是baseModel
                        if idx==0 and index == 0:
                            if label == candlabel:
                                e2lpd[candlabel] *= wm[worker]
                            else:
                                e2lpd[candlabel] *= (1 - wm[worker])

                            if candlabel == '0':
                                last00 = wm[worker]
                            if candlabel == '1':
                                last11 = wm[worker]
                            # last00 = wm[worker]
                            # last11 = wm[worker]
                            index = index + 1
                        else:
                            pij = wm[worker]

                            if candlabel == '0':
                                laste = last00
                            if candlabel == '1':
                                laste = last11
                            # laste = last0
                            if label==candlabel:
                                e2lpd[candlabel]*=pij + (1 -pij)*laste
                            else:
                                e2lpd[candlabel]*= 1-(pij + (1 -pij)*laste) # (1-wm[worker])*1.0/(len(self.label_set)-1)
                            if candlabel == '0':
                                last00 = pij + (1 -pij)*laste
                            if candlabel == '1':
                                last11 = pij + (1 -pij)*laste
                            # last0 = pij + (1 -pij)*laste
                            # if candlabel == '0':
                            #     last0 = e2lpd[e][candlabel]
                            # if candlabel == '1':
                            #     last1 = e2lpd[e][candlabel]
                            index = index + 1
                sums=0
                for label in self.label_set:
                    sums+=e2lpd[label]
                    if label == '0':
                        lastpd0 = e2lpd[label]
                    if label == '1':
                        lastpd1 = e2lpd[label]
                if sums==0:
                    for label in self.label_set:
                        e2lpd[label]=1.0/len(self.label_set)
                else:
                    for label in self.label_set:
                        e2lpd[label]=e2lpd[label]*1.0/sums
                e2l.append(e2lpd)
            result[e] = e2l
        #print e2lpd
        return result
def gete2wlandw2el(datafile):
    e2wl = {}
    w2el = {}
    label_set = []
    f = open(datafile, 'r')
    reader = csv.reader(f)
    next(reader)

    for line in reader:
        example, worker, label = line
        if example not in e2wl:
            e2wl[example] = []
        e2wl[example].append([worker, label])

        if worker not in w2el:
            w2el[worker] = []
        w2el[worker].append([example, label])

        if label not in label_set:
            label_set.append(label)

    return e2wl, w2el, label_set,

def geteworker(datafile):
    with open(datafile,'r') as load_f:
        weight = json.load(load_f)
        # print(weight)
    return weight

if __name__=='__main__':
    datafile = 'D:/fastwashdata/writers_38c3186f-f1a7-4faa-9917-695bc0e5ebe4.csv'
    e2wl,w2el,label_set = gete2wlandw2el(datafile)
    workers = geteworker("D:/fastwashdata/weight_file_gt_zc2.json")
    e2lpd = SubTaskDifficult(e2wl,w2el,label_set).ComputeDifficult(e2wl, workers)

    weight_file = 'D:/fastwashdata/weight_file_zc_compare3.json'
    with open(weight_file, 'wt') as f:
        json.dump(e2lpd, f)
