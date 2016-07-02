#!/usr/bin/env python

import rospy
import rostopic
from std_msgs.msg import String
from collections import defaultdict
import json
import requests
import rospy
import rostopic
from std_msgs.msg import String
import rosnode

URL = "http://192.168.0.15:8000/"

def parse_info_sub(info):
    reached_sub = False
    subscribers = []
    for l in info.splitlines():
        if reached_sub and not l:
            break
        elif reached_sub:
            parts = l.split(' ')
            if len(parts) < 3:
                rospy.loginfo("Something is wrong here!")
                continue
            subscribers.append(parts[2])
        elif l.startswith('Subscribers:'):
            reached_sub = True
    return subscribers


def parse_info_pub(info):
    reached_pub = False
    publishers = []
    for l in info.splitlines():
        if reached_pub and (not l or l.startswith('Subscribers:')):
            break
        elif reached_pub:
            parts = l.split(' ')
            if len(parts) < 3:
                rospy.loginfo("Something is wrong here!")
                continue
            publishers.append(parts[2])
        elif l.startswith('Publishers:'):
            reached_pub = True
    return publishers


def sendPub(new_publish):
    p = {}
    for topic in new_publish.keys():
        p[topic] = list(new_publish[topic])
    y = defaultdict(list)
    for key, values in p.iteritems():
        for value in values:
            y[value].append(key)

    for key in y.keys():
        new_key = key.replace("/", "__")
        y[new_key] = [item.replace("/", "__") for item in y.pop(key)]

    return y



def sendSub(new_publish):
    p = {}
    for topic in new_publish.keys():
        p[topic] = list(new_publish[topic])
    y = defaultdict(list)
    for key, values in p.iteritems():
        for value in values:
            y[value].append(key)

    for key in y.keys():
        new_key = key.replace("/", "__")
        y[new_key] = [item.replace("/", "__") for item in y.pop(key)]

    return y

def send_topics(topics):
    topics_dict = {}
    for topic, typ in topics:
        topics_dict[topic.replace("/", "__")] = typ
    return topics_dict


def send_nodes(nodes):
    y = {}
    y["nodes"] = nodes
    return [item.replace("/", "__") for item in nodes]


def arch():
    last_topics = []
    last_nodes = []
    last_publish = {}
    last_publish1 = {}

    while not rospy.is_shutdown():
        new_topics = rospy.get_published_topics()
        new_nodes = rosnode.get_node_names()

        new_publish = {}
        new_publish1 = {}

        for topic, typ in new_topics:
            info = rostopic.get_info_text(topic)
            subscribers = parse_info_sub(info)
            publishers = parse_info_pub(info)
            new_publish[topic] = set(subscribers)
            new_publish1[topic] = set(publishers)

        if new_topics != last_topics or new_nodes != last_nodes or new_publish != last_publish or  new_publish1 != last_publish1:
            y = {}
            y["topics"] = send_topics(new_topics)
            y["nodes"] = send_nodes(new_nodes)
            y["pub"] = sendPub(new_publish)
            y["sub"] = sendSub(new_publish1)

            last_nodes = new_nodes
            last_topics = new_topics
            last_publish = new_publish
            last_publish1 = new_publish1

            requests.get(URL + 'data', data=json.dumps(y))

if __name__ == '__main__':
    try:
        arch()
    except rospy.ROSInterruptException:
        pass