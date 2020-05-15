package ttt;

import lstar.AbstractLearningMethod;
import lstar.EquivalenceQuery;
import lstar.Membership;
import ota.*;


import java.util.*;

public class DTree extends AbstractLearningMethod {

    private Node root;
    private boolean isComplete = false;
    private Set<Track> trackSet = new HashSet<>();
    private Map<Location, Node> map;
    private Map<Node,Location> map1;


    public DTree(String name, Set<String> sigma, Membership membership){
        super(name,sigma,membership);

        root = new Node(TimeWords.EMPTY_WORDS,false,false);

        boolean isAccpted = answer(TimeWords.EMPTY_WORDS);
        if(isAccpted == false){
            Node left = new Node(TimeWords.EMPTY_WORDS,true,false);
            root.setLeftChild(left);
        }else {
            Node right = new Node(TimeWords.EMPTY_WORDS, true,true);
            root.setRightChild(right);
        }

        for(String action:sigma){
            TimeWord timeWord = new TimeWord(action,0);
            TimeWords timeWords = TimeWordsUtil.concat(TimeWords.EMPTY_WORDS,timeWord);
            Node node = sift(timeWords);
            TimeWords target;
            if(node == null){
                refine(timeWords);
                target = timeWords;
            }else {
                target = node.getSuffix();
            }
            Track track = new Track(TimeWords.EMPTY_WORDS,target,timeWord);
            trackSet.add(track);
        }
    }


    public Node sift(TimeWords words){
        Node currentNode = root;
        while (currentNode != null && !currentNode.isLeaf()){
            TimeWords suffix = currentNode.getSuffix();
            TimeWords timeWords = TimeWordsUtil.concat(words,suffix);
            boolean answer = answer(timeWords);
            if(answer){
                currentNode = currentNode.getRightChild();
            }else {
                currentNode = currentNode.getLeftChild();
            }
        }
        return currentNode;
    }

    @Override
    public void refine(TimeWords ce){
        if(!isComplete){
            complete(ce);
            return;
        }

        int j = errLocation(ce);
        if(j == -1){
            throw  new RuntimeException("错误反例");
        }

        TimeWord w =  w = ce.get(j);
        Location qu = getHypothesis().getLocation(ce.subWords(0,j));
        TimeWords u = map.get(qu).getSuffix();
        Location qv = map1.get(sift(TimeWordsUtil.concat(u,w)));



        List<Transition> transitionList = getHypothesis().getTransitions(qu,null,qv);
        boolean isPass = false;
        for(Transition t:transitionList){
            if(t.isPass(w)){
                isPass = true;
                break;
            }
        }

        if(!isPass){
            TimeWords source = map.get(qu).getSuffix();
            TimeWords target = map.get(qv).getSuffix();
            Iterator<Track> iterator = trackSet.iterator();
            while (iterator.hasNext()){
                Track t = iterator.next();
                if(t.getSource().equals(source) && t.getWord().equals(w)){
                    iterator.remove();
                }
            }
            Track track = new Track(source,target,w);
            trackSet.add(track);
        } else {
            Node vNode = map.get(qv);
            TimeWords words = ce.subWords(j+1,ce.size());
            TimeWords v = vNode.getSuffix();
            TimeWords newWords = TimeWordsUtil.concat(u,w);

            vNode.setSuffix(words);
            if(answer(TimeWordsUtil.concat(v,words))){
                boolean init1 = v.equals(TimeWords.EMPTY_WORDS);
                boolean accpted1 = answer(v);
                Node node1 = new Node(v,init1,accpted1);
                vNode.setRightChild(node1);
                boolean init2 = newWords.equals(TimeWords.EMPTY_WORDS);
                boolean accpted2 = answer(newWords);
                Node node2 = new Node(newWords,init2,accpted2);
                vNode.setLeftChild(node2);
            }else {
                boolean init1 = v.equals(TimeWords.EMPTY_WORDS);
                boolean accpted1 = answer(v);
                Node node1 = new Node(v,init1,accpted1);
                vNode.setLeftChild(node1);
                boolean init2 = newWords.equals(TimeWords.EMPTY_WORDS);
                boolean accpted2 = answer(newWords);
                Node node2 = new Node(newWords,init2,accpted2);
                vNode.setRightChild(node2);
            }

            //更新到v节点的迁移
            for(Track track:trackSet){
                if(track.getTarget().equals(v)){
                    TimeWords timeWords = TimeWordsUtil.concat(track.getSource(),track.getWord());
                    if(answer(TimeWordsUtil.concat(timeWords,words))){
                        track.setTarget(vNode.getRightChild().getSuffix());
                    }else {
                        track.setTarget(vNode.getLeftChild().getSuffix());
                    }
                }
            }

            //更新以新增节点为出发点的迁移
            for(String action:getSigma()){
                TimeWord timeWord = new TimeWord(action,0);
                TimeWords timeWords = TimeWordsUtil.concat(newWords,timeWord);
                TimeWords target = sift(timeWords).getSuffix();
                Track track = new Track(newWords,target,timeWord);
                trackSet.add(track);
            }

        }

    }

    private void complete(TimeWords ce){
        Node pre = null;
        Node current = root;
        while (!current.isLeaf()){
            pre = current;
            TimeWords timeWords = TimeWordsUtil.concat(ce,current.getSuffix());
            boolean answer = answer(timeWords);
            current = answer?current.getRightChild():current.getLeftChild();;

            if(current == null){
                boolean init = ce.equals(TimeWords.EMPTY_WORDS);
                Node node = new Node(ce,init,answer);
                if(answer){
                    pre.setRightChild(node);
                }else {
                    pre.setLeftChild(node);
                }
                isComplete = true;

                for(String action:getSigma()){
                    TimeWord timeWord = new TimeWord(action,0);
                    TimeWords w = TimeWordsUtil.concat(ce,timeWord);
                    TimeWords target = sift(w).getSuffix();
                    Track track = new Track(ce,target,timeWord);
                    trackSet.add(track);
                }
                break;
            }
        }
    }

    private int errLocation(TimeWords ce){
        for(int i = 0; i < ce.size(); i++){
            if(!answer(gama(ce,i)) == answer(gama(ce,i+1))){
                return i;
            }
        }
        return -1;
    }

    private TimeWords gama(TimeWords words, int i){
        TimeWords w = words.subWords(0,i);
        Location location = getHypothesis().getLocation(w);
        TimeWords prefix = map.get(location).getSuffix();
        TimeWords suffix = words.subWords(i,words.size());
        TimeWords timeWords = TimeWordsUtil.concat(prefix,suffix);
        return timeWords;
    }

    public Map<TimeWords, Node> getLeafMap(){
        Map<TimeWords, Node> leafMap = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()){
            Node node = queue.remove();
            if(node.isLeaf()){
                TimeWords suffix= node.getSuffix();
                leafMap.put(suffix,node);
            }
            else {
                Node left = node.getLeftChild();
                Node right = node.getRightChild();
                if(left!=null){
                    queue.add(left);
                }
                if(right!=null){
                    queue.add(right);
                }
            }
        }
        return leafMap;
    }



    public Node getLeaf(TimeWords timeWords){
        return getLeafMap().get(timeWords);
    }




    @Override
    public void buildHypothesis() {
        map = new HashMap<>();
        map1 = new HashMap<>();
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();
        Map<TimeWords, Node> leafMap = getLeafMap();
        List<Node> nodeList = new ArrayList<>(leafMap.values());
        for(int i = 0; i < nodeList.size(); i++){
            Node node = nodeList.get(i);
            Location location = new Location(i+1,getName()+i+1,node.isInit(),node.isAccpted());
            locationList.add(location);
            map.put(location,node);
            map1.put(node,location);
        }

        for(Track track: trackSet){
            Node sourceNode = leafMap.get(track.getSource());
            Node targetNode = leafMap.get(track.getTarget());

            Location sourceLocation = map1.get(sourceNode);
            Location targetLocation = map1.get(targetNode);
            TimeWord word = track.getWord();
            String action = word.getAction();
            TimeGuard timeGuard = new TimeGuard(word);
            Transition transition = new Transition(sourceLocation,targetLocation,timeGuard,getResetMap().get(action),action);
            transitionList.add(transition);
        }


        OTA evidenceOTA = new OTA(getName(),getSigma(),locationList,transitionList);

        OTA hypothesis = OTABuilder.evidToOTA(evidenceOTA);
        setHypothesis(hypothesis);
    }


}


