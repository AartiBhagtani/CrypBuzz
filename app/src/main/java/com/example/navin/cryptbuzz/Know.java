package com.example.navin.cryptbuzz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Know extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.know);

        CoinAdapter adapter = new CoinAdapter(this, generateData());

        ListView listView = (ListView) findViewById(R.id.coin_list);

        listView.setAdapter(adapter);
    }
    private ArrayList<Coins> generateData(){
        ArrayList<Coins> items = new ArrayList<Coins>();
        items.add(new Coins("Bitcoin [BTC]","The main reason bitcoin was created was to have a decentralized currency which is accepted throughout the world."));
        items.add(new Coins("Ethereum [ETH]","Ethereum is the second most popular platform in the crypto community and is often touted as bitcoin’s main rival.But unlike bitcoin, Ethereum was developed as a “world computer” super network of sorts, for the decentralised development of apps that would do away with third party companies like Google and Apple."));
        items.add(new Coins("Ripple [XRP]","Ripple seemingly came out of nowhere towards the end of 2017, when it briefly shot past Ethereum and then settled in third place in January 2018.Despite appearing to be a competitor to bitcoin, Ripple serves a different purpose and is in fact a centralised transaction network used by banks for money transfers just like SWIFT."));
        items.add(new Coins("Bitcoin Cash [BCH]","For a brief moment in August 2017, discord between bitcoin adopters over BTC’s technical limitations led to what is known as a fork in the blockchain."));
        items.add(new Coins("Cardano [ADA] ","Cardano is another platform used to send and receive digital money, employing the use of its digital token ADA."));
        items.add(new Coins("Stellar [XLM]","Stellar is another success story, having grown by 29,400 percent through 2017 alone.As an offshoot of Ripple, Stellar was launched in 2014 by Ripple co-founder Jed McCaleb and former lawyer Jouce Kim following internal disputes with Ripple."));
        items.add(new Coins("NEO","NEO is an emerging platform and digital token which enables the development of smart contracts and assets on the blockchain."));
        items.add(new Coins("Litecoin [LTC]","Litecoin is a peer-to-peer cryptocurrency and is often referred to as bitcoin’s little brother.But the most notable differences are litecoin’s much faster transaction speeds, and 84 million token limit and a more memory intensive mining process. EOS is another blockchain platform aiming to dethrone Ethereum as the go to infrastructure for decentralised apps."));
        items.add(new Coins("NEM XEM","NEM is a distributed blockchain and cryptocurrency with its signature XEM token"));
        return items;
    }
}

