package com.gestionexpedientes.demanda.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BpmnAreas {

    private static final Pattern AREA_LANE = Pattern.compile("<(?:bpmn:)?lane\\s[^>]*id=\"Lane_(\\d+)\"");

    private BpmnAreas() {
    }

    public static List<Integer> parse(String bpmnXml) {
        if (bpmnXml == null || bpmnXml.isBlank())
            return new ArrayList<>();

        Set<Integer> areas = new LinkedHashSet<>();
        Matcher matcher = AREA_LANE.matcher(bpmnXml);
        while (matcher.find())
            areas.add(Integer.parseInt(matcher.group(1)));

        return new ArrayList<>(areas);
    }
}
