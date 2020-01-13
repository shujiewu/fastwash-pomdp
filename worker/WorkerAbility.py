import copy
import sys
import random
import csv
import json


class WorkerAbility:

    def __init__(self, e2wl, w2el, label_set, w2e_set, e2t):

        self.e2wl = e2wl
        self.w2el = w2el
        self.label_set = label_set
        self.w2e_set = w2e_set
        self.e2t = e2t
        self.wm = self.InitWM(workers = {})

    def InitWM(self, workers):
        wm = {}
        if workers == {}:
            workers = self.w2el.keys()
            for worker in workers:
                wm[worker] = 0.8
        else:
            for worker in workers:
                if worker not in wm:  # workers --> wm
                    wm[worker] = 0.6
                else:
                    wm[worker] = workers[worker]
        return wm

    # E-step
    def ComputeTij(self, e2wl, l2pd, wm):
        e2lpd = {}

        for e, workerlabels in e2wl.items():
            if e in self.e2t:
                lpd = {}
                truth = e2t[e]
                for label in self.label_set:
                    if label == truth:
                        lpd[label] = 1.0
                    else:
                        lpd[label] = 0.0
                e2lpd[e] = lpd
                continue

            e2lpd[e] = {}
            for label in self.label_set:
                e2lpd[e][label] = 1.0  # l2pd[label]

            for worker, label in workerlabels:
                for candlabel in self.label_set:
                    if label == candlabel:
                        e2lpd[e][candlabel] *= wm[worker]
                    else:
                        e2lpd[e][candlabel] *= (1 - wm[worker]) * 1.0 / (len(self.label_set) - 1)

            sums = 0
            for label in self.label_set:
                sums += e2lpd[e][label]

            if sums == 0:
                for label in self.label_set:
                    e2lpd[e][label] = 1.0 / len(self.label_set)
            else:
                for label in self.label_set:
                    e2lpd[e][label] = e2lpd[e][label] * 1.0 / sums

        # print e2lpd
        return e2lpd

    # M-step
    def ComputePj(self, e2lpd):
        l2pd = {}

        for label in self.label_set:
            l2pd[label] = 0
        for e in e2lpd:
            for label in e2lpd[e]:
                l2pd[label] += e2lpd[e][label]

        for label in self.label_set:
            l2pd[label] = l2pd[label] * 1.0 / len(e2lpd)

        return l2pd

    def ComputeWM1(self, w2el, e2lpd):
        for worker, examplelabels in w2el.items():
            last = {}
            for e, workerlabels in self.e2wl.items():
                if e not in self.w2e_set[worker]:
                    continue
                last[e] = 0.0
                if e in self.e2t:
                    truth = e2t[e]
                    pos = 0
                    for w, label in workerlabels:
                        if w==worker:
                            break
                        if pos==0:
                            #last[e] = self.wm[worker]
                            if label==truth:
                               last[e] = self.wm[worker]
                            else:
                               last[e] = 1-self.wm[worker]
                        else:
                            #last[e] = self.wm[worker] + (1 - self.wm[worker]) * last[e]
                            if label==truth:
                                last[e] = self.wm[worker]+(1-self.wm[worker])*last[e]
                            else:
                                last[e] = 1-(self.wm[worker]+(1-self.wm[worker])*last[e])
                        pos = pos+1
            iter = 0
            while iter < 1000:
                gradient = 0.0
                for e, label in examplelabels:
                    if e in self.e2t:
                        truth = e2t[e]
                        if label == truth:
                            gradient+=(1-last[e])/(self.wm[worker]+(1-self.wm[worker])*last[e])
                        else:
                            gradient+=(last[e]-1)/(1-(self.wm[worker]+(1-self.wm[worker])*last[e]))
                    # self.wm[worker] += e2lpd[e][label] * 1.0 / len(examplelabels)
                #print(gradient)
                self.wm[worker] += 0.0001*gradient
                if self.wm[worker]>1.0:
                    self.wm[worker]=1.0
                if self.wm[worker]<0:
                    self.wm[worker]=0.0
                print(self.wm[worker])
                iter= iter+1
        return self.wm

    def ComputeWM(self, w2el, e2lpd):
        wm = {}
        for worker, examplelabels in w2el.items():
            wm[worker] = 0.0
            for e, label in examplelabels:
                wm[worker] += e2lpd[e][label] * 1.0 / len(examplelabels)
        return wm

    def Run(self, iter=5):
        # wm     worker_to_ability = {}
        # e2pd   example_to_softlabel = {}
        while iter > 0:
            e2lpd = self.ComputeTij(self.e2wl, {}, self.wm)
            self.wm = self.ComputeWM1(self.w2el, e2lpd)
            print(self.wm)
            iter -= 1
        return e2lpd, self.wm


def getaccuracy(truthfile, e2lpd, label_set):
    e2truth = {}
    f = open(truthfile, 'r')
    reader = csv.reader(f)
    next(reader)

    for line in reader:
        example, truth = line
        e2truth[example] = truth

    tcount = 0
    count = 0

    for e in e2lpd:

        if e not in e2truth:
            continue

        temp = 0
        for label in e2lpd[e]:
            if temp < e2lpd[e][label]:
                temp = e2lpd[e][label]

        candidate = []

        for label in e2lpd[e]:
            if temp == e2lpd[e][label]:
                candidate.append(label)

        truth = random.choice(candidate)

        count += 1

        if truth == e2truth[e]:
            tcount += 1

    return tcount * 1.0 / count


def gete2wlandw2el(datafile):
    e2wl = {}
    w2el = {}
    label_set = []
    w2e_set ={}
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
            w2e_set[worker] = set()
        w2el[worker].append([example, label])
        w2e_set[worker].add(example)

        if label not in label_set:
            label_set.append(label)

    return e2wl, w2el, label_set,w2e_set


def gete2t(known_true):
    e2t = {}
    f = open(known_true)
    reader = csv.reader(f)
    next(reader)

    for line in reader:
        example, truth = line
        e2t[example] = truth

    f.close()
    return e2t


if __name__ == '__main__':
    datafile = 'D:/fastwashdata/writers_38c3186f-f1a7-4faa-9917-695bc0e5ebe4.csv'
    e2wl, w2el, label_set,w2e_set = gete2wlandw2el(datafile)
    e2t = gete2t('D:/fastwashdata/gt_38c3186f-f1a7-4faa-9917-695bc0e5ebe4.csv')
    e2lpd, weight = WorkerAbility(e2wl, w2el, label_set,w2e_set, e2t).Run()

    weight_file = 'D:/fastwashdata/weight_file_gt_zc2.json'
    # config_file = 'D:/fastwashdata/config_file_gt_zc1.json'
    with open(weight_file, 'wt') as f:
        json.dump(weight, f)
    # with open(config_file, 'wt') as f:
    #     json.dump(e2lpd, f)

