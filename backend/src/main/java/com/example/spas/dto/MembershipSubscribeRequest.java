package com.example.spas.dto;

public class MembershipSubscribeRequest {

    private Long membershipId;

    // Constructors
    public MembershipSubscribeRequest() {
    }

    public MembershipSubscribeRequest(Long membershipId) {
        this.membershipId = membershipId;
    }

    // Getters and Setters
    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }
}